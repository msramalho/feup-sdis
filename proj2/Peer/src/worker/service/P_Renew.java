package src.worker.service;

import src.localStorage.StoredChunk;
import src.util.Message;
import src.worker.Dispatcher;
import src.worker.Protocol;

public class P_Renew extends Protocol {
    public P_Renew(Dispatcher d) { super(d); }

    @Override
    public void run() {

        for (StoredChunk storedChunk : d.peerConfig.internalState.storedChunks.values()) {
            if (storedChunk.fileId.equals(d.message.fileId) && storedChunk.chunkNo == d.message.chunkNo) {
                if (storedChunk.isSavedLocally()) {
                    d.peerConfig.internalState.renewChunk(storedChunk);
                    logger.print(String.format("1 expiration date(s) updated: fileId: %s, chunkNo: %d, expiration date: %s", storedChunk.fileId, storedChunk.chunkNo, storedChunk.getExpirationDate().toString()));
                }
            }
        }
    }
}
