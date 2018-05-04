package src.localStorage;

import src.util.Message;

import java.io.Serializable;

public class StoredChunk extends Chunk implements Serializable {
    public transient boolean receivedPutChunk = false;
    boolean savedLocally = false; //true if this peer has a copy of the chunk, false otherwise
    public boolean inProcess = false; //true if this Chunk is being handled in a GETCHUNK received message

    // TODO: test if constructor is needed for serialization
    public StoredChunk() { }

    public StoredChunk(Message message) {super(message);}

    public boolean isSavedLocally() { return savedLocally; }

    void setSavedLocally(boolean savedLocally) { this.savedLocally = savedLocally; }


    @Override
    public String toString() {
        return "StoredChunk{" +
                "fileId='" + fileId.substring(0, 10) + '\'' +
                ", chunkNo=" + chunkNo +
                ", savedLocally=" + savedLocally +
                ", deleted=" + deleted+
                ", replicationDegree=" + replicationDegree +
                ", peersAcks= (" + peersAcks.size() + ")" + peersAcks +
                '}';
    }
}
