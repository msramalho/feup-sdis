package src.worker;

import src.localStorage.LocalChunk;
import src.main.PeerConfig;
import src.util.Message;

public class RenewChunk implements Runnable {
    // private static int REMOVE_ATTEMPTS = 5;
    private PeerConfig peerConfig;
    private LocalChunk localChunk;

    public RenewChunk(PeerConfig peerConfig, LocalChunk localChunk) {
        this.peerConfig = peerConfig;
        this.localChunk = localChunk;
    }

    @Override
    public void run() {

        // sendLine RENEW Message for others to hear
        peerConfig.multicast.control.send(Message.create("RENEW %s %d %s %d", peerConfig.protocolVersion, peerConfig.id, localChunk.fileId, localChunk.chunkNo));
    }
}
