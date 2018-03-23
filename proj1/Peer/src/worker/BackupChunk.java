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

    public BackupChunk(PeerConfig peerConfig, LocalChunk localChunk) {
        this.peerConfig = peerConfig;
        this.localChunk = localChunk;
    }

    @Override
    public void run() {
        //create message to send and convert to byte array
        String message = String.format("PUTCHUNK %s %d %s %d %d \r\n\r\n %s", peerConfig.protocolVersion, peerConfig.id, localChunk.file.fileId, localChunk.chunkNo, localChunk.file.replicationDegree, new String(localChunk.chunk));

        //wait for STORED replies
        int replies = 0, i;
        for (i = 1; i <= BackupChunk.PUTCHUNK_ATTEMPTS && replies < localChunk.file.replicationDegree; i++) {
            //create and send message through multicast
            peerConfig.mcBackup.send(message);
            // System.out.println("[BackupChunk] - sent chunk: " + localChunk.chunkNo + "(" + message.length() + " bytes): " + message.substring(0, 25));

            int wait = (int) Math.pow(2, i) * 1000; // calculate the wait delay in milliseconds
            System.out.println("[BackupChunk] - waiting for  " + wait + "ms (got " + replies + "/" + localChunk.file.replicationDegree + " replies)");
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
            }
            replies += this.getRepliesWithTimeout();
        }
        System.out.println("[BackupChunk] - backup completed after " + (i - 1) + " attempts, with " + replies + "/" + localChunk.file.replicationDegree + " replies");

        peerConfig.internalState.addLocalFile(localChunk.file).save();
        //TODO: commit the number of replies to the database
    }

    // count the number of STORED messages in the queue for the current chunk
    private int getRepliesWithTimeout() {
        int replies = 0;
        Message expectedMessage = new Message("STORED", localChunk.file.fileId, localChunk.chunkNo);

        while (peerConfig.mcControl.mcQueue.remove(expectedMessage))
            replies++;

        return replies;
    }
}
