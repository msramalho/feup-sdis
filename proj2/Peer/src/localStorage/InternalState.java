package src.localStorage;

import src.main.PeerConfig;
import src.util.Logger;
import src.util.Message;
import src.worker.RemoveChunk;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class InternalState implements Serializable {
    static transient String internalStateFolder = "internal_state_peer_%d";
    private static transient String internalStateFilename = "database.ser";
    private static transient Logger logger = new Logger("InternalState");
    public long occupiedSpace; // bytes that this peer can occupy in the file system
    public long allowedSpace = (long) (16 * Math.pow(2, 20)); // bytes that this peer can occupy in the file system
    // public long allowedSpace = (long) (1 * Math.pow(2, 17)); // bytes that this peer can occupy in the file system
    
    public String encryptionKey;

    private static int peerId;

    public ConcurrentHashMap<String, LocalChunk> localChunks; // local files being backed up - file_id_chukNo => LocalChunk
    public ConcurrentHashMap<String, StoredChunk> storedChunks; // others' chunks - <file_id>_<chunkNo> => StoredChunk

    private InternalState() {
        this.localChunks = new ConcurrentHashMap<>();
        this.storedChunks = new ConcurrentHashMap<>();
    }

    //------------------------------ file system funcions above

    /**
     * receives the current peerId and loads the serialized values from the correspondent folder. If there is no database (file or folder) a new and empty one is created
     *
     * @param pId the peerid of the internal state
     * @return InternalState
     */
    public static InternalState load(int pId) {
        peerId = pId;
        internalStateFolder = String.format(internalStateFolder, peerId);
        InternalState is = null;
        try {
            FileInputStream fileIn = new FileInputStream(getDatabaseName());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            is = (InternalState) in.readObject();
            in.close();
            fileIn.close();
        } catch (Exception e) {
            logger.print("unable to load the 'database' file, may not exist yet");
            // e.printStackTrace();
        }
        if (is == null) is = new InternalState();

        is.createIfNotExists();//create internal state folder if it does not exist
        is.updateOccupiedSpace();//read the current occupied space

        return is;
    }

    public synchronized void save() {
        createIfNotExists();
        try {
            FileOutputStream fileOut = new FileOutputStream(getDatabaseName());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.flush();
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
        updateOccupiedSpace();
    }

    public void asyncChecks() { // works like a one-time garbage collector
        // try to delete all the chunks that failed to delete contents on disc when DELETE came
        for (StoredChunk sChunk : storedChunks.values())
            if (sChunk.deleted && sChunk.savedLocally)
                deleteStoredChunk(sChunk, true);
        removeEmptyFolders(internalStateFolder);
    }

    //recursively remove empty folders inside a folder
    private long removeEmptyFolders(String dir) {
        File f = new File(dir);
        if (!f.exists()) return 0;
        String listFiles[] = f.list();
        long totalSize = 0;
        assert listFiles != null;
        for (String file : listFiles) {
            File folder = new File(dir + "/" + file);
            if (folder.isDirectory())
                totalSize += removeEmptyFolders(folder.getAbsolutePath());
            else
                totalSize += folder.length();
        }

        if (totalSize == 0) f.delete();

        return totalSize;
    }

    private void createIfNotExists() {
        File directory = new File(internalStateFolder);
        if (!directory.exists()) directory.mkdir();
        try {
            new File(getDatabaseName()).createNewFile(); // create if not exists
        } catch (IOException e) {
            logger.print("unable to create or load file");
            e.printStackTrace();
        }

    }

    private void updateOccupiedSpace() { occupiedSpace = InternalState.folderSize(new File(internalStateFolder)); }

    private long getFileSpace(String filename) { return new File(filename).length(); }

    private static long folderSize(File directory) {
        long length = 0;
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    public long availableSpace() { updateOccupiedSpace(); return allowedSpace - occupiedSpace; }

    public boolean freeMemory(PeerConfig peerConfig, long requiredMemory) {
        ArrayList<RemoveChunk> tasks = new ArrayList<>();
        long canFree = 0;
        for (StoredChunk sChunk : storedChunks.values()) {
            //This chunk is replicated more times than it should
            if (sChunk.savedLocally && sChunk.peersAcks.size() > sChunk.replicationDegree) {
                tasks.add(new RemoveChunk(peerConfig, sChunk));
                canFree += getFileSpace(getChunkPath(sChunk));
                if (canFree >= requiredMemory) break;
            }
        }
        if (canFree < requiredMemory) return false;//unable to free the desired space

        //if the desired space can be freed, do it asynchronously and return false
        for (RemoveChunk rChunk : tasks)
            peerConfig.threadPool.submit(rChunk);
        return true;
    }
    //------------------------------ local and stored chunks

    // return the local chunk or null if it does not exist
    public LocalChunk getLocalChunk(Message m) { return getLocalChunk(Chunk.getUniqueId(m.fileId, m.chunkNo)); }

    public LocalChunk getLocalChunk(String uniqueId) { return localChunks.get(uniqueId); }

    // return the stored chunk or null if it does not exist
    public synchronized StoredChunk getStoredChunk(Message m) {
        StoredChunk sChunk = storedChunks.get(Chunk.getUniqueId(m.fileId, m.chunkNo));
        if (sChunk != null && sChunk.savedLocally && sChunk.chunk == null) {
            logger.print(String.format("reading (%s) from memory", sChunk.getShortId()));
            Path p = FileSystems.getDefault().getPath("", getChunkPath(sChunk));
            try {
                sChunk.chunk = Files.readAllBytes(p);
            } catch (IOException e) {
                logger.print(String.format("unable to read chunk from memory (%s) this chunk will be marked as unsaved locally", sChunk.getUniqueId()));
                sChunk.savedLocally = false;
                save();
                e.printStackTrace();
            }
        }
        return sChunk;
    }

    //add or update a given local chunk information
    public void addLocalChunk(LocalChunk localChunk) {
        localChunks.put(localChunk.getUniqueId(), localChunk);
    }

    //add or update a given stored chunk  information
    public InternalState addStoredChunk(StoredChunk storedChunk) {
        storedChunks.put(storedChunk.getUniqueId(), storedChunk);
        return this;
    }

    public void saveChunkLocally(StoredChunk sChunk) {
        try {
            Path path = Paths.get(getChunkPath(sChunk));
            Files.createDirectories(path.getParent());

            FileOutputStream fos = new FileOutputStream(getChunkPath(sChunk));
            fos.write(sChunk.chunk);
            fos.close();

            sChunk.setSavedLocally(true);
            sChunk.deleted = false;
            sChunk.addAck(peerId);
        } catch (IOException i) {
            logger.print("failed to save chunk " + sChunk.getUniqueId() + " locally");
            i.printStackTrace();
        }
    }

    public boolean deleteStoredChunk(StoredChunk sChunk, boolean dueToDELETE) {
        boolean res = false;
        File file = new File(getChunkPath(sChunk));
        if (file.delete()) {
            logger.print("chunk: " + sChunk.getShortId() + " deleted");
            sChunk.savedLocally = false;
            sChunk.peersAcks.remove(peerId);
            res = true;
        } else {
            logger.err("unable to delete chunk: " + sChunk.getUniqueId());
        }
        sChunk.deleted = dueToDELETE;
        save();
        return res;
    }

    public void reclaimKBytes(int maxDiskSpace) {
        allowedSpace = maxDiskSpace * 1000;
        if (maxDiskSpace == 0) // remove all files
            for (StoredChunk storedChunk : storedChunks.values())
                if (deleteStoredChunk(storedChunk, false))
                    storedChunk.savedLocally = false;

        save();
        logger.print("allowedSpace after: " + allowedSpace);
    }

    public void addMissingInfoForClient() {

    }
    
    public void addKey(String key) {
        encryptionKey = key;
        save();
    }
    //------------------------------ util functions

    @Override
    public String toString() {
        String spacePercent = allowedSpace == 0 ? "inf" : String.valueOf(Math.round(100 * occupiedSpace / allowedSpace));
        return "InternalState{\n" +
                "   localChunks(" + localChunks.size() + ")=" + localChunks.values() +
                "\n   storedChunks(" + storedChunks.size() + ")=" + storedChunks.values() +
                "\n   occupied: " + occupiedSpace + "/" + allowedSpace + " = " + spacePercent + "%" +
                "}";
    }

    // return the path to the chunk in this peer's filesystem
    private String getChunkPath(StoredChunk sChunk) { return internalStateFolder + "/" + sChunk.fileId + "/" + sChunk.chunkNo; }

    private static String getDatabaseName() { return internalStateFolder + "/" + internalStateFilename; }

}
