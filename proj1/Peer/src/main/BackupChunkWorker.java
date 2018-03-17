package main;

import util.Message;

import java.io.IOException;
import java.net.*;

//PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
public class BackupChunkWorker implements Runnable {
    private static int PUTCHUNK_ATTEMPTS = 5;
    private PeerConfig peerConfig;
    private byte[] chunk;
    private int chunkNo;
    private int replicationDeg;

    public BackupChunkWorker(PeerConfig peerConfig, byte[] chunk, int chunkNo, int replicationDeg) {
        this.peerConfig = peerConfig;
        this.chunk = chunk;
        this.chunkNo = chunkNo;
        this.replicationDeg = replicationDeg;
    }

    @Override
    public void run() {
        //create message to send and convert to byte array
        String message = String.format("PUTCHUNK %s %d %d %d \r\n\r\n %s", peerConfig.protocolVersion, peerConfig.id, this.chunkNo, this.replicationDeg, new String(this.chunk));
        byte[] data = message.getBytes();

        //create and send package
        this.sendChunkPacket(data);

        //wait for responses
        for (int i = 1; i <= BackupChunkWorker.PUTCHUNK_ATTEMPTS; i++) {
            int wait = (int) Math.pow(2, i) * 1000; // calculate the wait delay in milliseconds
            int replies = this.getRepliesWithTimeout(wait);
        }
    }

    private int getRepliesWithTimeout(int wait) {
        int replies = 0;
        Message message;
        while (true) {
            try {
                //this.peerConfig.mcControl.setSoTimeout(wait);
                message = this.peerConfig.mcControl.mcQueue.take();
                System.out.println("MESSAGE: " + message.body);
                if (message.isBackup()) replies++;
                else this.peerConfig.mcControl.mcQueue.putLast(message); //add back to the queue if it is not ours
            } catch (InterruptedException e) {
                System.err.println("[BackupChunkWorker] - PeerConfig.mcQueue.take() ended abruptly");
                e.printStackTrace();
            }
        }
    }

    //simply send the PUTCHUNK packet
    private synchronized void sendChunkPacket(byte[] data) {
        try {
            DatagramPacket outPacket = new DatagramPacket(data, data.length, this.peerConfig.mcBackup.getGroup(), this.peerConfig.mcBackup.getLocalPort()); // create the packet to send through the socket
            this.peerConfig.mcBackup.send(outPacket);
            System.out.println("sent chunk");
        } catch (IOException e) {
            System.err.println("[BackupChunkWorker] - cannot send PUTCHUNK to mcBackup");
            e.printStackTrace();
        }
    }
}
