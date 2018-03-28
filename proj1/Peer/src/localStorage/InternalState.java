package src.localStorage;

import src.util.Message;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

public class InternalState implements Serializable {
    public static transient String internalStateFolder = "internal_state_peer_%d";
    private static transient String internalStateFilename = "database.ser";
    public static int peerId;

    ConcurrentHashMap<String, LocalChunk> localChunks; // local files being backed up - file_id => LocalFile
    ConcurrentHashMap<String, StoredChunk> storedChunks; // others' chunks - <file_id>_<chunkNo> => StoredChunk

    public InternalState() {
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
            System.out.println("[InternalState] - unable to load the 'database' file, may not exist yet");
            // e.printStackTrace();
        }
        if (is == null) is = new InternalState();

        return is;
    }

    public void save() {
        System.out.println("[InternalState] - saving " + getDatabaseName());
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
    }

    private void createIfNotExists() {
        File directory = new File(internalStateFolder);
        if (!directory.exists()) directory.mkdir();
        try {
            new File(getDatabaseName()).createNewFile(); // create if not exists
        } catch (IOException e) {
            System.out.println("[InternalState] - unable to create or load file");
            e.printStackTrace();
        }

    }

    //------------------------------ local and stored chunks

    // return the local chunk or null if it does not exist
    public LocalChunk getLocalChunk(Message m) { return getLocalChunk(Chunk.getUniqueId(m.fileId, m.chunkNo)); }

    public LocalChunk getLocalChunk(String uniqueId) { return localChunks.get(uniqueId); }

    // return the stored chunk or null if it does not exist
    public StoredChunk getStoredChunk(Message m) { return storedChunks.get(Chunk.getUniqueId(m.fileId, m.chunkNo)); }

    //add or update a given local chunk information
    public InternalState addLocalChunk(LocalChunk localChunk) {
        localChunks.put(localChunk.getUniqueId(), localChunk);
        return this;
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
            sChunk.addAck(peerId);
            // System.out.println("[InternalState] - chunk " + sChunk.chunkNo + " saved successfully");
        } catch (IOException i) {
            System.out.println("[InternalState] - failed to save chunk " + sChunk.getUniqueId() + " locally");
            i.printStackTrace();
        }
    }


    //------------------------------ util functions

    @Override
    public String toString() {
        return "InternalState{\n" +
                "   localChunks=" + localChunks.size() +
                "\n   storedChunks=" + storedChunks.size() +
                "\n}";
    }

    // return the path to the chunk in this peer's filesystem
    private String getChunkPath(StoredChunk sChunk) {
        return internalStateFolder + "/" + sChunk.fileId + "/" + sChunk.chunkNo;
    }

    private static String getDatabaseName() {
        return internalStateFolder + "/" + internalStateFilename;
    }

}
