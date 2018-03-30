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
        byte[] message = Message.createMessage(String.format("DELETE %s %d %s\r\n\r\n", peerConfig.protocolVersion, peerConfig.id, localChunk.fileId));
        peerConfig.mcControl.send(message); // send DELETE message
        //update local information about this deleted chunk
        peerConfig.internalState.save();
        localChunk.deleted = true;

        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        peerConfig.mcControl.send(message);

        //TODO: enhancement receive acks for delete (DELETED) messages
        //TODO: enhancement trigger this mechanism on ADELE Protocol (Hello)

    }
}
