package src.worker;

import src.localStorage.LocalChunk;

import java.util.ArrayList;

public class P_Adele extends Protocol {
    P_Adele(Dispatcher d) {super(d);}

    @Override
    public void run() {

        ArrayList<String> filesFound = new ArrayList<>();
        // iterate over all local chunks
        for (LocalChunk localChunk : d.peerConfig.internalState.localChunks.values()) {
            // if this chunk has been deleted and the peer that said HELLO is saving this chunk, send DELETE
            if (localChunk.deleted && localChunk.peersAcks.contains(d.message.senderId) && !filesFound.contains(localChunk.deleted)) {
                filesFound.add(localChunk.fileId);
                d.peerConfig.threadPool.submit(new DeleteFile(d.peerConfig, localChunk));
            }
        }
    }
}
