package src.localStorage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

public class InternalState implements Serializable {
    private static transient String internalStateFolder = "internal_state_peer_%d";
    private static transient String internalStateFilename = "database.ser";
    private static transient String internalStorage = "storage";
    public static int peerId;

    ConcurrentHashMap<String, LocalFile> localFiles; // local files being backed up - file_id => LocalFile
    ConcurrentHashMap<String, StoredChunk> storedChunks; // others' chunks - <file_id>_<chunkNo> => StoredChunk

    public InternalState() {
        this.localFiles = new ConcurrentHashMap<>();
        this.storedChunks = new ConcurrentHashMap<>();
    }

    /**
     * receives the current peerId and loads the json values from the correspondent folder. If there is no database (file or folder) a new and empty one is created
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

    public void saveChunkLocally(StoredChunk chunk, String body) {
        System.out.println("[InternalState] Added in: " + getChunkPath(chunk.fileId, chunk.chunkNumber));
        try {
            Path path = Paths.get(getChunkPath(chunk.fileId, chunk.chunkNumber));
            Files.createDirectories(path.getParent());
            Files.write(path, body.getBytes());
            storedChunks.put(chunk.fileId + chunk.chunkNumber, chunk);
            System.out.println("[InternalState] - chunk saved locally & hashmap");

        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    //add or update a given local file information
    public InternalState addLocalFile(LocalFile localFile) {
        localFiles.put(localFile.fileId, localFile);
        return this;
    }

    public boolean isLocalFile(String fileId) {
        return localFiles.containsKey(fileId);
    }

    public StoredChunk getStoredChunk(String fileId, int chunkNo) {
        return storedChunks.get(fileId + chunkNo);
    }

    @Override
    public String toString() {
        return "InternalState{" +
                "localFiles=" + localFiles.size() +
                '}';
    }

    private String getChunkPath(String fileId, int chunkId) {
        return internalStorage + "/" + peerId + "/" + fileId + "/" + chunkId;
    }

    private static String getDatabaseName() {
        return internalStateFolder + "/" + internalStateFilename;
    }

    public void display() {
        System.out.println("[InternalState] - " + this.toString());
    }

}
