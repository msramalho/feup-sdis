package src.localStorage;

import src.util.Message;

import java.util.Objects;

public class StoredChunk extends Chunk {
    boolean savedLocally; //true if this peer has a copy of the chunk, false otherwise

    public StoredChunk() { }

    public StoredChunk(Message m) {
        this(m.fileId, m.chunkNo, m.replicationDegree, m.getBodyBytes());
    }


    public StoredChunk(String fileId, int chunkNo, int replicationDegree, byte[] chunk) {
        super(fileId, chunkNo, replicationDegree, chunk);
        savedLocally = false;
    }

    public boolean isSavedLocally() { return savedLocally; }

    public void setSavedLocally(boolean savedLocally) { this.savedLocally = savedLocally; }
}
