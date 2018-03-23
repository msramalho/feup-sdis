package src.main;

import src.localStorage.LocalChunk;
import src.util.Message;

import java.io.IOException;
import java.net.*;


//PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
public class BackupChunkWorker implements Runnable {
    private static int PUTCHUNK_ATTEMPTS = 5;
    private PeerConfig peerConfig;
    private LocalChunk localChunk;
    private int replicationDeg;

    public BackupChunkWorker(PeerConfig peerConfig, LocalChunk localChunk, int replicationDeg) {
        this.peerConfig = peerConfig;
        this.localChunk = localChunk;
        this.replicationDeg = replicationDeg;
    }

    @Override
    public void run() {
        //create message to send and convert to byte array
        String message = String.format("PUTCHUNK %s %d %s %d %d \r\n\r\n %s", peerConfig.protocolVersion, peerConfig.id, localChunk.fileId, localChunk.chunkNo, replicationDeg, new String(localChunk.chunk));
        byte[] data = message.getBytes();
        //create and send package
        this.sendChunkPacket(data);

        //wait for STORED replies
        int replies = 0;
        for (int i = 1; i <= BackupChunkWorker.PUTCHUNK_ATTEMPTS || replies > this.replicationDeg; i++) {
            int wait = (int) Math.pow(2, i) * 1000; // calculate the wait delay in milliseconds
            System.out.println("[BackupChunkWorker] - waiting for  " + wait + "ms");
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
            }
            replies += this.getRepliesWithTimeout(wait);
            System.out.println("[BackupChunkWorker] - got " + replies + " replies");
        }
        System.out.println("[BackupChunkWorker] - gave up on sending: " + replies + " replies");
        //TODO: commit the number of replies to the database
    }

    // count the number of STORED messages in the queue for the current chunk
    private int getRepliesWithTimeout(int wait) {
        int replies = 0;
        Message expectedMessage = new Message("STORED", localChunk.fileId, localChunk.chunkNo);
        while (peerConfig.mcControl.mcQueue.remove(expectedMessage)) {
            System.out.println("FOUND and REMOVED");
            replies++;
        }
        return replies;
    }

    //simply send the PUTCHUNK packet
    private synchronized void sendChunkPacket(byte[] data) {
        try {
            DatagramPacket outPacket = new DatagramPacket(data, data.length, peerConfig.mcBackup.getGroup(), peerConfig.mcBackup.getLocalPort()); // create the packet to send through the socket
            peerConfig.mcBackup.send(outPacket);
            System.out.println("[BackupChunkWorker] - sent chunk: " + localChunk.chunkNo + "(" + data.length + " bytes): " + new String(data).substring(0, 25) + "...");
        } catch (IOException e) {
            System.err.println("[BackupChunkWorker] - cannot send PUTCHUNK to mcBackup");
            e.printStackTrace();
        }
    }
}
