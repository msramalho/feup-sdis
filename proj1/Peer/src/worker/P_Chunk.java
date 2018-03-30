package src.worker;

import src.localStorage.Chunk;
import src.localStorage.LocalChunk;

public class P_Chunk extends Protocol {
    public P_Chunk(Dispatcher d) { super(d); }

    @Override
    public void run() {
        Chunk chunk;

        // check if this CHUNK is about a StoredChunk
        if ((chunk = d.peerConfig.internalState.getStoredChunk(d.message)) != null) {
            chunk.gotAnswer = true;
        } // if not, check if this CHUNK is about a LocalChunk
        else if ((chunk = d.peerConfig.internalState.getLocalChunk(d.message)) != null) {
            chunk.gotAnswer = true;
            if (!d.peerConfig.isEnhanced() || ((LocalChunk) chunk).socket == null)
                chunk.chunk = d.message.body; // save the received value
            else  // if the socket was created successfully
                ((LocalChunk) chunk).loadFromTCP(); // ENHANCEMENT_2
        }
    }
}
