package main;

import java.io.IOException;
import java.net.*;
import java.sql.SQLSyntaxErrorException;

public class BackupChunkWorker implements Runnable {
    PeerConfig peerConfig;
    byte[] chunk;
    int chunkNo;
    int replicationDeg;

    public BackupChunkWorker(PeerConfig peerConfig, byte[] chunk, int chunkNo, int replicationDeg) {
        this.peerConfig = peerConfig;
        this.chunk = chunk;
        this.chunkNo = chunkNo;
        this.replicationDeg = replicationDeg;
    }

    /**
     * PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
     */
    @Override
    public void run() {
        String message = String.format("PUTCHUNK %s %d %d %d \r\n\r\n %s", peerConfig.protocolVersion, peerConfig.id, this.chunkNo, this.replicationDeg, new String(this.chunk));
        byte[] data = message.getBytes();

        //socket set up

        try {
            DatagramPacket outPacket = new DatagramPacket(data, data.length, this.peerConfig.mcBackup.getGroup(), this.peerConfig.mcBackup.getLocalPort()); // create the packet to send through the socket
            this.peerConfig.mcBackup.send(outPacket);
        } catch (IOException e) {
            System.out.println("[BackupChunkWorker] - cannot send PUTCHUNK to mcBackup");
            e.printStackTrace();
        }
    }
}
