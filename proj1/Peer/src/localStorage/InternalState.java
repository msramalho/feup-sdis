package src.localStorage;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.HashMap;

public class InternalState {
    private static transient String internalStateFolder = "internal_state_peer_%d";
    private static transient String internalStateFilename = "database.json";

    HashMap<String, LocalFile> localFiles; // local files being backed up
    // ArrayList<StoredChunk> storedChunks;


    public InternalState() {
        this.localFiles = new HashMap<>();
    }

    /**
     * receives the current peerId and loads the json values from the correspondent folder. If there is no database (file or folder) a new and empty one is created
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
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(getDatabaseName()));
            is = gson.fromJson(reader, InternalState.class);
        } catch (IOException e) {
            System.out.println("[InternalState] - unable to read (or create) the 'database' file");
            e.printStackTrace();
        }
        if (is == null) is = new InternalState();

        return is;
    }

    public void save() {
        Gson gson = new Gson();
        System.out.println("saving " + getDatabaseName());
        try {
            FileWriter fw = new FileWriter(getDatabaseName());
            gson.toJson(this, fw);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
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
