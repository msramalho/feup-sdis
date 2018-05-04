package src.worker;

import src.localStorage.LocalChunk;

public class P_Deleted extends Protocol {
    P_Deleted(Dispatcher d) { super(d); }

    @Override
    public void run() {
        // iterate over all local chunks
        for (LocalChunk localChunk : d.peerConfig.internalState.localChunks.values()) {
            logger.print(localChunk.toString());
            // if this chunk has been deleted and the peer that said HELLO is saving this chunk, send DELETE
            localChunk.peersAcks.remove(d.message.senderId);
        }

        // commit changes to non-volatile memory
        d.peerConfig.internalState.save();
    }
}
