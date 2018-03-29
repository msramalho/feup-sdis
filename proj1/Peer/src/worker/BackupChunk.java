package src.worker;

import src.localStorage.LocalChunk;
import src.main.PeerConfig;
import src.util.Message;

//PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
public class BackupChunk implements Runnable {
    private static int PUTCHUNK_ATTEMPTS = 5;
    private PeerConfig peerConfig;
    private LocalChunk localChunk;

    public BackupChunk(PeerConfig peerConfig, LocalChunk localChunk) {
        this.peerConfig = peerConfig;
        this.localChunk = localChunk;
    }

    @Override
    public void run() {
        LocalChunk lChunk = peerConfig.internalState.getLocalChunk(localChunk.getUniqueId());


        // try to read the chunk from the internal state, and add it if it is not there.
        if (lChunk == null) {
            lChunk = localChunk;
            peerConfig.internalState.addLocalChunk(lChunk);
        } else {
            System.out.println("[BackupChunk] - chunk: " + lChunk + " is already backed up");
            lChunk.chunk = localChunk.chunk;
            // return;
            // this local chunk is already being sent by the current peer, abort} and has enough copies - could have if (lChunk.replicationDegree <= lChunk.peersAcks.size()), but nothing is said
        }

        //create message to send and convert to byte array
        byte[] message = Message.createMessage(String.format("PUTCHUNK %s %d %s %d %d\r\n\r\n", peerConfig.protocolVersion, peerConfig.id, lChunk.fileId, lChunk.chunkNo, lChunk.replicationDegree), lChunk.chunk);

        //wait for STORED replies
        int i = 0;
        do {
            peerConfig.mcBackup.send(message); // send message through multicast
            int wait = (int) Math.pow(2, i) * 1000; // calculate the wait delay in milliseconds

            System.out.println("[BackupChunk] - waiting for chunk " + lChunk.chunkNo + " " + wait + "ms (got " + lChunk.countAcks() + "/" + lChunk.replicationDegree + " replies)");

            try { Thread.sleep(wait); } catch (InterruptedException e) {}
            i++;
        } while (i < BackupChunk.PUTCHUNK_ATTEMPTS && lChunk.countAcks() < lChunk.replicationDegree);


        System.out.println("[BackupChunk] - backup completed after " + (i) + " attempt(s), with " + lChunk.countAcks() + "/" + localChunk.replicationDegree + " replies");

        peerConfig.internalState.save();
    }
}
