package src.worker;

import src.localStorage.LocalChunk;
import src.main.PeerConfig;
import src.util.Message;

import java.io.IOException;
import java.net.*;


//PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
public class BackupChunk implements Runnable {
    private static int PUTCHUNK_ATTEMPTS = 5;
    private PeerConfig peerConfig;
    private LocalChunk localChunk;
    private int replicationDeg;

    public BackupChunk(PeerConfig peerConfig, LocalChunk localChunk, int replicationDeg) {
        this.peerConfig = peerConfig;
        this.localChunk = localChunk;
        this.replicationDeg = replicationDeg;
    }

    @Override
    public void run() {
        //create message to send and convert to byte array
        String message = String.format("PUTCHUNK %s %d %s %d %d \r\n\r\n %s", peerConfig.protocolVersion, peerConfig.id, localChunk.file.fileId, localChunk.chunkNo, replicationDeg, new String(localChunk.chunk));

        //wait for STORED replies
        int replies = 0;
        for (int i = 1; i <= BackupChunk.PUTCHUNK_ATTEMPTS || replies > this.replicationDeg; i++) {
            //create and send message through multicast
            peerConfig.mcBackup.send(message);
            // System.out.println("[BackupChunk] - sent chunk: " + localChunk.chunkNo + "(" + message.length() + " bytes): " + message.substring(0, 25));

            int wait = (int) Math.pow(2, i) * 1000; // calculate the wait delay in milliseconds
            System.out.println("[BackupChunk] - waiting for  " + wait + "ms");
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
            }
            replies += this.getRepliesWithTimeout(wait);
            System.out.println("[BackupChunk] - got " + replies + " replies");
        }
        System.out.println("[BackupChunk] - gave up on sending: " + replies + " replies");
        //TODO: commit the number of replies to the database
    }

    // count the number of STORED messages in the queue for the current chunk
    private int getRepliesWithTimeout(int wait) {
        int replies = 0;
        Message expectedMessage = new Message("STORED", localChunk.file.fileId, localChunk.chunkNo);
        while (peerConfig.mcControl.mcQueue.remove(expectedMessage)) {
            System.out.println("FOUND and REMOVED");
            replies++;
        }
        return replies;
    }
}
