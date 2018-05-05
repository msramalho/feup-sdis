package src.worker.Service;

import src.localStorage.StoredChunk;
import src.util.Message;
import src.worker.Dispatcher;
import src.worker.Protocol;

public class P_Delete extends Protocol {
    public P_Delete(Dispatcher d) { super(d); }

    @Override
    public void run() {

        int count = 0;
        boolean hasChunk = false;

        for (StoredChunk storedChunk : d.peerConfig.internalState.storedChunks.values()) {
            if (storedChunk.fileId.equals(d.message.fileId)) {
                hasChunk = true;
                if (storedChunk.isSavedLocally()) {
                    d.peerConfig.internalState.deleteStoredChunk(storedChunk, true);
                    count++;
                }
            }
        }

        if (hasChunk) {
            d.peerConfig.multicast.control.send(Message.create("DELETED %s %d %s\r\n\r\n", d.peerConfig.protocolVersion, d.peerConfig.id, d.message.fileId));
        }

        // commit changes to non-volatile memory
        d.peerConfig.internalState.save();

        logger.print(String.format("deleted %d chunk(s)", count));
    }
}
