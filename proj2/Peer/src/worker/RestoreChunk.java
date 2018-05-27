package src.worker;

import src.localStorage.LocalChunk;
import src.main.PeerConfig;
import src.util.Logger;
import src.util.Message;

import java.net.UnknownHostException;
import java.util.concurrent.Callable;

// sendLine GETCHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
// wait for CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>
public class RestoreChunk implements Callable {
    private static int RESTORE_ATTEMPTS = 5;
    private PeerConfig peerConfig;
    private LocalChunk lChunk;
    private Logger logger = new Logger("RestoreChunk");

    public RestoreChunk(PeerConfig peerConfig, LocalChunk lChunk) {
        this.peerConfig = peerConfig;
        this.lChunk = lChunk;
    }

    @Override
    public Object call() {
        LocalChunk lChunk = peerConfig.internalState.getLocalChunk(this.lChunk.getUniqueId());
        if (lChunk == null) return null; // this is not a local file
        lChunk.chunk = null; // equivalent to empty cache

        // handle ENHANCEMENT_2
        byte[] getChunkBody = new byte[0];
        if (peerConfig.isEnhanced() && lChunk.startTCP()) {
            try {
                // if is enhanced, sendLine the IP:Port of the TCP connection to the peer and TCP started succesfully
                getChunkBody = lChunk.tcp.getCoordinates().getBytes();
            } catch (UnknownHostException e) {
                logger.err("Unable to get Host to sendLine TCP coordinates in GETCHUNK");
            }
        }

        byte[] message = Message.create("GETCHUNK %s %d %s %d", getChunkBody, peerConfig.protocolVersion, peerConfig.id, lChunk.fileId, lChunk.chunkNo);

        for (int i = 0; i < RestoreChunk.RESTORE_ATTEMPTS && lChunk.chunk == null; i++) {
            //no ongoing TCP connection - this will be false again if TCP connection fails
            if (lChunk.noTcp() || i == 0) peerConfig.multicast.control.send(message); //create and sendLine message through multicast

            int wait = (int) Math.pow(2, i) * 1000; // calculate the wait delay in milliseconds
            logger.print("waiting for CHUNK " + lChunk.chunkNo + " for " + wait + "ms");
            try { Thread.sleep(wait); } catch (InterruptedException ignored) {}
        }
        return lChunk;
    }
}
