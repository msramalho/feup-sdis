package src.worker.service;

import src.localStorage.StoredChunk;
import src.util.Message;
import src.worker.Dispatcher;
import src.worker.Protocol;

public class P_Renew extends Protocol {
    public P_Renew(Dispatcher d) { super(d); }

    @Override
    public void run() {

        int count = 0;
        boolean hasChunk = false;

        for (StoredChunk storedChunk : d.peerConfig.internalState.storedChunks.values()) {
            if (storedChunk.fileId.equals(d.message.fileId)) {
                hasChunk = true;
                if (storedChunk.isSavedLocally()) {
                    d.peerConfig.internalState.renewChunk(storedChunk);
                    count++;
                }
            }
        }

        logger.print(String.format("%d expiration date(s) updated", count));
    }
}
