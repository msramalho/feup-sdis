package src.localStorage;

import src.util.Message;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

public class InternalState implements Serializable {
    private static transient String internalStateFolder = "internal_state_peer_%d";
    private static transient String internalStateFilename = "database.ser";
    public static int peerId;

    ConcurrentHashMap<String, LocalChunk> localChunks; // local files being backed up - file_id => LocalFile
    ConcurrentHashMap<String, StoredChunk> storedChunks; // others' chunks - <file_id>_<chunkNo> => StoredChunk

    public InternalState() {
        this.localChunks = new ConcurrentHashMap<>();
        this.storedChunks = new ConcurrentHashMap<>();
    }

    /**
     * receives the current peerId and loads the serialized values from the correspondent folder. If there is no database (file or folder) a new and empty one is created
     *
     * @param pId the peerid of the internal state
     * @return InternalState
     */
    public static InternalState load(int pId) {
        peerId = pId;
        internalStateFolder = String.format(internalStateFolder, peerId);
        File directory = new File(internalStateFolder);
        if (!directory.exists()) directory.mkdir();
        try {
            new File(getDatabaseName()).createNewFile(); // create if not exists
        } catch (IOException e) {
            System.out.println("[InternalState] - unable to create or load file");
            e.printStackTrace();
        }

        InternalState is = null;
        try {
            FileInputStream fileIn = new FileInputStream(getDatabaseName());
            ObjectInputStream in = new ObjectInputStream(fileIn);
            is = (InternalState) in.readObject();
            in.close();
            fileIn.close();
        } catch (Exception i) {
            System.out.println("[InternalState] - unable to load the 'database' file, may not exist yet");
        }
        if (is == null) is = new InternalState();

        return is;
    }

    public void save() {
        System.out.println("saving " + getDatabaseName());
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

    public void saveChunkLocally(StoredChunk sChunk) {
        System.out.println("[InternalState]  - Adding chunk to: " + getChunkPath(sChunk));
        try {
            Path path = Paths.get(getChunkPath(sChunk));
            Files.createDirectories(path.getParent());
            Files.write(path, sChunk.chunk);
            sChunk.setSavedLocally(true);
            System.out.println("[InternalState] - chunk " + sChunk.getUniqueId() + " saved successfully" );
        } catch (IOException i) {
            System.out.println("[InternalState] - failed to save chunk " + sChunk.getUniqueId() + " locally" );
            i.printStackTrace();
        }
    }

    //add or update a given local file information
    public InternalState addLocalChunk(LocalChunk localChunk) {
        localChunks.put(localChunk.getUniqueId(), localChunk);
        return this;
    }

    public boolean isLocalFile(String fileId) {
        return localChunks.containsKey(fileId);
    }

    public StoredChunk getStoredChunk(Message m) {
        return storedChunks.get(StoredChunk.getUniqueId(m.fileId, m.chunkNo));
    }

    public synchronized boolean lockChunk(StoredChunk storedChunk) {
        if (storedChunks.containsKey(storedChunk.getUniqueId())) return storedChunk.isLocked();
        storedChunks.put(storedChunk.getUniqueId(), storedChunk);
        return true;
    }


    //------------------------------

    @Override
    public String toString() {
        return "InternalState{" +
                "localChunks=" + localChunks.size() +
                '}';
    }

    // return the path to the chunk in this peer's filesystem
    private String getChunkPath(StoredChunk sChunk) {
        return internalStateFolder + "/" + sChunk.fileId + "/" + sChunk.chunkNo;
    }

    private static String getDatabaseName() {
        return internalStateFolder + "/" + internalStateFilename;
    }

    public void display() {
        System.out.println("[InternalState] - " + this.toString());
    }

}
