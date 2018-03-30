package src.worker;

import src.localStorage.LocalChunk;

public class P_Hello extends Protocol {
    public P_Hello(Dispatcher d) {super(d);}

    @Override
    public void run() {
        // iterate over all local chunks
        for (LocalChunk localChunk : d.peerConfig.internalState.localChunks.values()) {
            // if this chunk has been deleted and the peer that said HELLO is saving this chunk, send DELETE
            if (localChunk.deleted && localChunk.peersAcks.contains(d.message.senderId)) {
                d.peerConfig.threadPool.submit(new DeleteFile(d.peerConfig, localChunk));
            }
        }
    }
}
