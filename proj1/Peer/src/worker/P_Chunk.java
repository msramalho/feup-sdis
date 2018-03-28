package src.worker;

import src.localStorage.Chunk;
import src.localStorage.StoredChunk;

public class P_Chunk extends Protocol {
    public P_Chunk(Dispatcher d) { super(d); }

    @Override
    public void run() {
        Chunk chunk;

        // check if this CHUNK is about a StoredChunk
        if ((chunk = d.peerConfig.internalState.getStoredChunk(d.message)) != null) {
            ((StoredChunk) chunk).gotAnswer = true;
        } // if not, check if this CHUNK is about a LocalChunk
        else if ((chunk = d.peerConfig.internalState.getLocalChunk(d.message)) != null) {
            chunk.chunk = d.message.body; // save the received value
        }

    }
}
