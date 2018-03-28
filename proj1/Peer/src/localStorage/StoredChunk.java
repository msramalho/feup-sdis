package src.localStorage;

import src.util.Message;

import java.io.Serializable;
import java.util.Objects;

public class StoredChunk extends Chunk implements Serializable {
    boolean savedLocally; //true if this peer has a copy of the chunk, false otherwise
    public transient boolean locked; // true if there is a Worker handling a PUTCHUNK for this chunk

    public StoredChunk(Message m) {
        this(m.fileId, m.chunkNo, m.replicationDegree, m.getBodyBytes());
    }


    public StoredChunk(String fileId, int chunkNo, int replicationDegree, byte[] chunk) {
        super(fileId, chunkNo, replicationDegree, chunk);
        savedLocally = false;
        locked = false;
    }

    public void setSavedLocally(boolean savedLocally) {
        this.savedLocally = savedLocally;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoredChunk that = (StoredChunk) o;
        return chunkNo == that.chunkNo && fileId.equals(that.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId, chunkNo);
    }

}
