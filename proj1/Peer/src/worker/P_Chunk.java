package src.worker;

import src.localStorage.Chunk;
import src.localStorage.LocalChunk;
import src.main.PeerConfig;

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
            //only use TCP if this Peer is enhanced, if the Peer that sent the CHUNK message is enhanced and if this Peer was able to create the socket
            if (!d.peerConfig.isEnhanced() || !PeerConfig.isMessageEnhanced(d.message) || ((LocalChunk) chunk).socket == null)
                chunk.chunk = d.message.body; // save the received value
            else  // if the socket was created successfully
                ((LocalChunk) chunk).loadFromTCP(); // ENHANCEMENT_2
        }
    }
}
