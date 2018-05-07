package src.util;

import src.localStorage.LocalFile;

import java.io.*;
import java.net.*;

public class TcpServer {
    private Logger logger = new Logger(this);
    private ServerSocket socket;
    private Socket connectionSocket = null;

    public TcpServer() { }

    public boolean start() {
        try {
            socket = new ServerSocket(0);
            socket.setSoTimeout(300);
            socket.setReceiveBufferSize(LocalFile.CHUNK_SIZE);
            return true;
        } catch (IOException e) {
            logger.err("unable to open new socket, maybe all ports are being used");
            e.printStackTrace();
        }
        return false;
    }

    public boolean dead() { return socket == null; }

    public String getCoordinates() throws UnknownHostException {
        assertSocket();
        return String.format("%s:%s", InetAddress.getLocalHost().getHostAddress(), socket.getLocalPort());
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            logger.print("Unable to close TCP ServerSocket");
        }
    }

    public byte[] receive() {
        try {
            // prepare socket
            singletonConnectionSocket();
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
            logger.err("unable to receive data from TCP");
            e.printStackTrace();
        }
        return null;
    }

    public boolean sendLine(String data) {
        assertSocket();
        try {
            logger.print("sending...");
            singletonConnectionSocket();
            DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
            outToServer.writeBytes(data + "\n");
            outToServer.flush();
            // socket.close(); // sends EOF
            return true;
        } catch (IOException e) {
            logger.err("Unable to connect to TCP");
            e.printStackTrace(); // TODO: remove after test
        }
        return false;
    }

    public String readLine() {
        assertSocket();
        try {
            logger.print("reading...");
            singletonConnectionSocket();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            String res = inFromClient.readLine();
            logger.print(res);
            return res;
        } catch (IOException e) {
            logger.err("Unable to read line:");
            e.printStackTrace(); // TODO: remove after test
        }
        return null;
    }

    private void singletonConnectionSocket() throws IOException {
        if (connectionSocket == null) {
            connectionSocket = socket.accept();
        }
    }

    private void assertSocket() { assert socket != null; }
}
