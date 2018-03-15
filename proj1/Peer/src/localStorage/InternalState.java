package localStorage;

import main.PeerConfig;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class InternalState {
    private static String internalStateDir = "internal-state"; // path to folder where non-volatile memory is used
    private static String internalStateFilename = "database.json";
    ArrayList<LocalFile> localFiles; // local files being backed up
    ArrayList<StoredChunk> storedChunks;
    PeerConfig peerConfig;

    public InternalState(PeerConfig peerConfig) {
        this.peerConfig = peerConfig;

        this.loadStorage();
    }

    private void loadStorage() {
        this.storedChunks = new ArrayList<>();
        this.localFiles = new ArrayList<>();
        //TODO: check if any local file was deleted
    }

    public void saveStorage() {
        /*ArrayList<LocalChunk> lcs = new ArrayList<>();
        lcs.add(new LocalChunk(1, 0));
        lcs.add(new LocalChunk(2, 3));
        lcs.add(new LocalChunk(3, 2));
        this.localFiles.add(new LocalFile("sha256", "ficheiro1", lcs, 4));

        Gson gson = new Gson();
        String json = gson.toJson(this.localFiles);

        try {
            Files.write(Paths.get(this.internalStateFilename), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    public void backupFile(String filename) {
        //try to backup the file, by adding to the localFiles and saving to memory
        //split the file into parts
    }
}
