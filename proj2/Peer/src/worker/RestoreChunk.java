package src.worker;

import src.localStorage.LocalChunk;
import src.main.PeerConfig;
import src.util.Message;

import java.util.concurrent.Callable;

// send GETCHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
// wait for CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>
public class RestoreChunk implements Callable {
    private static int RESTORE_ATTEMPTS = 5;
    private PeerConfig peerConfig;
    private LocalChunk localChunk;

    public RestoreChunk(PeerConfig peerConfig, LocalChunk localChunk) {
        this.peerConfig = peerConfig;
        this.localChunk = localChunk;
    }

    @Override
    public Object call() {
        LocalChunk lChunk = peerConfig.internalState.getLocalChunk(localChunk.getUniqueId());
        if (lChunk == null) return null; // this is not a local file
        lChunk.chunk = null; // equivalent to empty cache

        // handle ENHANCEMENT_2
        byte[] getChunkBody = new byte[0];
        if (peerConfig.isEnhanced() && peerConfig.machineIp != null && lChunk.startTCP()) {
            // if is enhanced, send the IP:Port of the TCP connection to the peer and TCP started succesfully
            String chunkBody = String.format("%s:%s", peerConfig.machineIp.getHostAddress(), lChunk.tcp.socket.getLocalPort());
            getChunkBody = chunkBody.getBytes();
        }

        byte[] message = Message.createMessage(String.format("GETCHUNK %s %d %s %d \r\n\r\n", peerConfig.protocolVersion, peerConfig.id, lChunk.fileId, lChunk.chunkNo), getChunkBody);

        for (int i = 0; i < RestoreChunk.RESTORE_ATTEMPTS && lChunk.chunk == null; i++) {
            //no ongoing TCP connection - this will be false again if TCP connection fails
            if (lChunk.tcp.dead() || i == 0) peerConfig.mcControl.send(message); //create and send message through multicast

            int wait = (int) Math.pow(2, i) * 1000; // calculate the wait delay in milliseconds
            System.out.println("[RestoreChunk] - waiting for CHUNK " + lChunk.chunkNo + " for " + wait + "ms");
            try { Thread.sleep(wait); } catch (InterruptedException e) {}
        }
        return lChunk;
    }
}
