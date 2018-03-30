package src.localStorage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class LocalChunk extends Chunk implements Serializable {
    public transient ServerSocket socket = null; //socket used for TCP connection for the current chunk

    public LocalChunk(String fileId, int chunkNo) { super(fileId, chunkNo); }

    public LocalChunk(String fileId, int chunkNo, Integer replicationDegree, byte[] chunk) {super(fileId, chunkNo, replicationDegree, chunk); }

    public void initiateSocket() {
        try {
            socket = new ServerSocket(0);
        } catch (IOException e) {
            System.out.println("[LocalChunk] - unable to open new socket, maybe all ports are being used");
            e.printStackTrace();
        }
    }

    // uses the previously opened ServerSocket to receive the chunk bytes through TCP
    public void loadFromTCP() throws IOException {
        socket.setReceiveBufferSize(LocalFile.CHUNK_SIZE);
        socket.setSoTimeout(250);
        Socket connectionSocket = socket.accept();
        socket.close();
        DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());

        int totalRead = 0, lastRead = 1;
        byte[] tempChunk = new byte[LocalFile.CHUNK_SIZE];
        while (totalRead < LocalFile.CHUNK_SIZE && lastRead >= 0) {
            lastRead = inFromClient.read(tempChunk, totalRead, 64000 - totalRead);
            totalRead += lastRead > 0 ? lastRead : 0; // only update for positive values
        }
        chunk = new byte[totalRead];
        for (int i = 0; i < totalRead; i++)
            chunk[i] = tempChunk[i];

        System.out.println("[LocalChunk.loadFromTCP] - read: " + totalRead + " bytes");
        socket = null;
    }


}
