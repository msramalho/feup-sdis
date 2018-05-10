package src.util;

import src.localStorage.LocalFile;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;

public class TcpServer extends Tcp {
    private SSLServerSocket serverSocket;

    public boolean start() {
        //Set properties for SSL connection. Key Store for Sercer Peer
        try {
            System.setProperty("javax.net.ssl.keyStore", "src/util/ssl/server.keys");
            System.setProperty("javax.net.ssl.keyStorePassword","123456");
            System.setProperty("javax.net.ssl.trustStore", "src/util/ssl/truststore");
            System.setProperty("javax.net.ssl.trustStorePassword", "123456");


            SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(0);
            serverSocket.setNeedClientAuth(true);  // s is an SSLServerSocket
            serverSocket.setSoTimeout(300);
            serverSocket.setEnabledCipherSuites(new String[]{"SSL_RSA_WITH_RC4_128_MD5"});
            String protocols[] = {"SSL_RSA_WITH_RC4_128_MD5"};
            serverSocket.setEnabledProtocols(protocols);
            serverSocket.setEnableSessionCreation(false);
            serverSocket.setUseClientMode(false);
            serverSocket.setNeedClientAuth(false);

            //Wating for cliente... to do handshake
            socket = (SSLSocket) serverSocket.accept();
            socket.startHandshake();
            logger.print("CipherSuite available: SSL_RSA_WITH_RC4_128_MD5");
            

            //SSL SIMPLE
            //serverSocket = new ServerSocket(0);
            /*serverSocket = ssf.createServerSocket(0); 
            serverSocket.setSoTimeout(300);
            serverSocket.setReceiveBufferSize(LocalFile.CHUNK_SIZE);

            return true;

        } catch (IOException e) {
            logger.err("Unable to open new serverSocket, maybe all ports are being used: " + e.getMessage());
        }
        return false;
    }

    public boolean dead() { return socket == null; }

    public String getCoordinates() throws UnknownHostException {
        return String.format("%s:%s", InetAddress.getLocalHost().getHostAddress(), socket.getLocalPort());
    }

    public byte[] receive() {

        System.out.println("WILL RECEIVE");

        try {
            // prepare serverSocket
            socketChecks();
            socket.close();
            //System.out.print("SERVEEEEERRR " + serverSocket.getUseClientMode());
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
            socket = null;
            logger.err("unable to receive data from TCP SSLClient: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void socketChecks() throws IOException {
        if (socket == null) socket = (SSLSocket) serverSocket.accept();
    }


}
