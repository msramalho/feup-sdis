package src.worker;

import src.localStorage.StoredChunk;
import src.main.PeerConfig;
import src.util.Message;

public class RemoveChunk implements Runnable {
    // private static int REMOVE_ATTEMPTS = 5;
    private PeerConfig peerConfig;
    private StoredChunk storedChunk;

    public RemoveChunk(PeerConfig peerConfig, StoredChunk storedChunk) {
        this.peerConfig = peerConfig;
        this.storedChunk = storedChunk;
    }

    @Override
    public void run() {
        // delete from local storage
        peerConfig.internalState.deleteStoredChunk(storedChunk, false);

        // send REMOVE Message for others to hear
        peerConfig.multicast.control.send(Message.createMessage(String.format("REMOVED %s %d %s %d \r\n\r\n", peerConfig.protocolVersion, peerConfig.id, storedChunk.fileId, storedChunk.chunkNo)));
    }
}
