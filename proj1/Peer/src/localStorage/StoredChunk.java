package src.localStorage;

import src.util.Message;

public class StoredChunk extends Chunk {
    boolean savedLocally = false; //true if this peer has a copy of the chunk, false otherwise
    public boolean inProcess = false; //true if this Chunk is being handled in a GETCHUNK received message
    public boolean gotAnswer = false; // true if the current peer saw a CHUNK message while sleeping

    public StoredChunk() { }

    public StoredChunk(Message message) {super(message);}

    public boolean isSavedLocally() { return savedLocally; }

    public void setSavedLocally(boolean savedLocally) { this.savedLocally = savedLocally; }
}
