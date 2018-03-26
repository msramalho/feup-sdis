package src.localStorage;

import java.io.*;
import java.util.HashMap;

public class InternalState implements Serializable {
    private static transient String internalStateFolder = "internal_state_peer_%d";
    private static transient String internalStateFilename = "database.ser";

    HashMap<String, LocalFile> localFiles; // local files being backed up
    // ArrayList<StoredChunk> storedChunks;


    public InternalState() {
        this.localFiles = new HashMap<>();
    }

    /**
     * receives the current peerId and loads the json values from the correspondent folder. If there is no database (file or folder) a new and empty one is created
     *
     * @param peerId the peerid of the internal state
     * @return InternalState
     */
    public static InternalState load(int peerId) {
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
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    //add or update a given local file information
    public InternalState addLocalFile(LocalFile localFile) {
        localFiles.put(localFile.fileId, localFile);
        return this;
    }

    @Override
    public String toString() {
        return "InternalState{" +
                "localFiles=" + localFiles.size() +
                '}';
    }

    private static String getDatabaseName() {
        return internalStateFolder + "/" + internalStateFilename;
    }

    public void display() {
        System.out.println("[InternalState] - " + this.toString());
    }
}
