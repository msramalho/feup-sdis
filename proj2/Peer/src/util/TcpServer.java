package src.util;

import src.localStorage.LocalFile;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.*;

public class TcpServer extends Tcp {
    private SSLServerSocket serverSocket;

    public boolean start() {
        try {
            System.setProperty("javax.net.ssl.keyStore", "src/util/ssl/server.keys");
            System.setProperty("javax.net.ssl.keyStorePassword","123456");
            System.setProperty("javax.net.ssl.trustStore", "src/util/ssl/truststore");
            System.setProperty("javax.net.ssl.trustStorePassword", "123456");


            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(0);
            serverSocket.setNeedClientAuth(true);  // s is an SSLServerSocket
            serverSocket.setSoTimeout(300);
            serverSocket.setReceiveBufferSize(LocalFile.CHUNK_SIZE);

            return true;
        } catch (IOException e) {
            logger.err("unable to open new serverSocket, maybe all ports are being used: " + e.getMessage());
        }
        return false;
    }

    public boolean dead() { return serverSocket == null; }

    public String getCoordinates() throws UnknownHostException {
        return String.format("%s:%s", InetAddress.getLocalHost().getHostAddress(), serverSocket.getLocalPort());
    }

    public byte[] receive() {
        try {
            // prepare serverSocket
            socketChecks();
            serverSocket.close();
            DataInputStream inFromClient = new DataInputStream(socket.getInputStream());

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
            serverSocket = null;
            logger.err("unable to receive data from TCP: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void socketChecks() throws IOException {
        if (socket == null) socket = (SSLSocket) serverSocket.accept();
    }


}
