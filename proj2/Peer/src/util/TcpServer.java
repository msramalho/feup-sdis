package src.util;

import src.localStorage.LocalFile;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpServer {
    private Logger logger = new Logger(this);
    private ServerSocket socket;

    public TcpServer() { }

    public boolean start() {
        try {
            socket = new ServerSocket(0);
            return true;
        } catch (IOException e) {
            logger.err("unable to open new socket, maybe all ports are being used");
            e.printStackTrace();
        }
        return false;
    }

    public boolean dead() { return socket == null; }

    public String getCoordinates() throws UnknownHostException {
        return String.format("%s:%s", InetAddress.getLocalHost().getHostAddress(), socket.getLocalPort());
    }

    public byte[] receive() {
        try {
            // prepare socket
            socket.setReceiveBufferSize(LocalFile.CHUNK_SIZE);
            socket.setSoTimeout(300);
            Socket connectionSocket = socket.accept();
            socket.close();
            DataInputStream inFromClient = new DataInputStream(connectionSocket.getInputStream());

            // read into byte[]
            int totalRead = 0, lastRead = 1;
            byte[] tempChunk = new byte[LocalFile.CHUNK_SIZE];
            while (totalRead < LocalFile.CHUNK_SIZE && lastRead >= 0) {
                lastRead = inFromClient.read(tempChunk, totalRead, LocalFile.CHUNK_SIZE - totalRead);
                totalRead += lastRead > 0 ? lastRead : 0; // only update for positive values
            }

            logger.print("read: " + totalRead + " bytes from tcp");
            byte[] received = new byte[totalRead];
            System.arraycopy(tempChunk, 0, received, 0, totalRead);

            return received;
        } catch (IOException e) {
            socket = null;
            logger.err("unable to receive chunk through TCP, defaulting back to old protocol");
            e.printStackTrace();
        }
        return null;
    }
}
