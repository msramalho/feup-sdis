package src.worker;

import src.localStorage.LocalChunk;
import src.main.PeerConfig;
import src.util.Message;

public class DeleteFile implements Runnable {
    // private static int DELETE_ATTEMPTS = 5;
    private PeerConfig peerConfig;
    private LocalChunk localChunk;

    public DeleteFile(PeerConfig peerConfig, LocalChunk localChunk) {
        this.peerConfig = peerConfig;
        this.localChunk = localChunk;
    }

    @Override
    public void run() {
        // send DELETE message
        peerConfig.mcControl.send(Message.createMessage(String.format("DELETE %s %d %s\r\n\r\n", peerConfig.protocolVersion, peerConfig.id, localChunk.fileId)));

        // update localChunks to know they have been deleted
        for (LocalChunk lChunk: peerConfig.internalState.localChunks.values()) {
            if (lChunk.fileId.equals(localChunk.fileId)) {
                lChunk.deleted = true;
            }
        }
        
        peerConfig.internalState.save();
    }
}
