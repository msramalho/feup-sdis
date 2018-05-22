package src.worker;

import src.localStorage.Chunk;
import src.localStorage.LocalChunk;
import src.main.PeerConfig;
import src.util.Logger;
import src.util.Message;

//PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
public class BackupChunk implements Runnable {
    private static int PUTCHUNK_ATTEMPTS = 5;
    private PeerConfig peerConfig;
    private Chunk initialChunk;
    private boolean isLocalChunk;
    private Logger logger = new Logger("BackupChunk");

    public BackupChunk(PeerConfig peerConfig, Chunk initialChunk, boolean isLocalChunk) {
        this.peerConfig = peerConfig;
        this.initialChunk = initialChunk;
        this.isLocalChunk = isLocalChunk;
    }

    @Override
    public void run() {
        Chunk c;
        if (!isLocalChunk) { // this corresponds to sending PUTCHUNK after receiving REMOVED
            c = initialChunk;
        } else { // this corresponds to a request to Backup a chunk from the TestApp or InitiatorPeer
            c = peerConfig.internalState.getLocalChunk(initialChunk.getUniqueId());

            // try to read the chunk from the internal state, and add it if it is not there.
            if (c == null) {
                c = initialChunk;
                peerConfig.internalState.addLocalChunk((LocalChunk) c);
            } else {
                logger.print("chunk: " + c + " is already backed up");
                c.chunk = initialChunk.chunk;
                c.decryptBytes();
                // return;
                // this local chunk is already being sent by the current peer, abort} and has enough copies - could have if (lChunk.replicationDegree <= lChunk.peersAcks.size()), but nothing is said
            }
            peerConfig.internalState.save();
        }

        //create message to send and convert to byte array
        byte[] message = Message.create("PUTCHUNK %s %d %s %d %d", c.chunk, peerConfig.protocolVersion, peerConfig.id, c.fileId, c.chunkNo, c.replicationDegree);

        //wait for STORED replies
        int i = 0;
        do {
            peerConfig.multicast.backup.send(message); // send message through multicast
            int wait = (int) Math.pow(2, i) * 1000; // calculate the wait delay in milliseconds

            logger.print("waiting for chunk " + c.chunkNo + " " + wait + "ms (got " + c.countAcks() + "/" + c.replicationDegree + " replies)");

            try { Thread.sleep(wait); } catch (InterruptedException ignored) {}
            i++;
        } while (i < BackupChunk.PUTCHUNK_ATTEMPTS && c.countAcks() < c.replicationDegree);

        logger.print("backup completed after " + (i) + " attempt(s), with " + c.countAcks() + "/" + initialChunk.replicationDegree + " replies");
    }
}
