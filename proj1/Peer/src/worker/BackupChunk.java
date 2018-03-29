package src.worker;

import src.localStorage.Chunk;
import src.localStorage.LocalChunk;
import src.main.PeerConfig;
import src.util.Message;

//PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
public class BackupChunk implements Runnable {
    private static int PUTCHUNK_ATTEMPTS = 5;
    private PeerConfig peerConfig;
    private Chunk initialChunk;
    private boolean isLocalChunk;

    public BackupChunk(PeerConfig peerConfig, Chunk initialChunk, boolean isLocalChunk) {
        this.peerConfig = peerConfig;
        this.initialChunk = initialChunk;
        this.isLocalChunk = isLocalChunk;
    }

    @Override
    public void run() {
        System.out.println("BACKING UP..................");
        Chunk c = null;
        if (!isLocalChunk) { // this corresponds to sending PUTCHUNK after receiving REMOVED
            c = initialChunk;
        } else { // this corresponds to a request to Backup a chunk from the TestApp or InitiatorPeer
            c = peerConfig.internalState.getLocalChunk(initialChunk.getUniqueId());

            // try to read the chunk from the internal state, and add it if it is not there.
            if (c == null) {
                c = initialChunk;
                peerConfig.internalState.addLocalChunk((LocalChunk) c);
            } else {
                System.out.println("[BackupChunk] - chunk: " + c + " is already backed up");
                c.chunk = initialChunk.chunk;
                // return;
                // this local chunk is already being sent by the current peer, abort} and has enough copies - could have if (lChunk.replicationDegree <= lChunk.peersAcks.size()), but nothing is said
            }
        }

        //create message to send and convert to byte array
        byte[] message = Message.createMessage(String.format("PUTCHUNK %s %d %s %d %d\r\n\r\n", peerConfig.protocolVersion, peerConfig.id, c.fileId, c.chunkNo, c.replicationDegree), c.chunk);

        //wait for STORED replies
        int i = 0;
        do {
            peerConfig.mcBackup.send(message); // send message through multicast
            int wait = (int) Math.pow(2, i) * 1000; // calculate the wait delay in milliseconds

            System.out.println("[BackupChunk] - waiting for chunk " + c.chunkNo + " " + wait + "ms (got " + c.countAcks() + "/" + c.replicationDegree + " replies)");

            try { Thread.sleep(wait); } catch (InterruptedException e) {}
            i++;
        } while (i < BackupChunk.PUTCHUNK_ATTEMPTS && c.countAcks() < c.replicationDegree);

        System.out.println("[BackupChunk] - backup completed after " + (i) + " attempt(s), with " + c.countAcks() + "/" + initialChunk.replicationDegree + " replies");

        peerConfig.internalState.save();
    }
}
