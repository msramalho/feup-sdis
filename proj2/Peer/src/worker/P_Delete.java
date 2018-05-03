package src.worker;

import src.localStorage.StoredChunk;
import src.util.Message;

public class P_Delete extends Protocol {
    P_Delete(Dispatcher d) { super(d); }

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
            d.peerConfig.mcControl.send(Message.createMessage(String.format("DELETED %s %d %s\r\n\r\n", d.peerConfig.protocolVersion, d.peerConfig.id, d.message.fileId)));
        }

        // commit changes to non-volatile memory
        d.peerConfig.internalState.save();

        System.out.println(String.format("[Protocol:Delete] - deleted %d chunk(s)", count));
    }
}
