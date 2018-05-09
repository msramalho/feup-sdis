package src.util;

import src.localStorage.LocalFile;

import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;

public class TcpServer extends Tcp {
    SSLServerSocket serverSocket;
    // TODO Add cipher to SSL Socket
    public boolean start() {
        //Set properties for SSL connection. Key Store for Sercer Peer
        try {
            char[] passphrase = "sdis18".toCharArray();
            KeyStore keystore = null;
            SSLServerSocketFactory ssf = null;
            // Read MyKeyStore For Server Side
            try {
                keystore = KeyStore.getInstance("JKS");
                keystore.load(new FileInputStream("src/util/mykeystore/examplestore"), passphrase);
            } catch (Exception e) {
                logger.err("Cant find File " + e.getMessage());
            }

            try {
                // Each key manager manages a specific type of key material for use by secure sockets
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                //Get Session started with key
                kmf.init(keystore, passphrase);
                SSLContext context = SSLContext.getInstance("TLS");
                KeyManager[] keyManagers = kmf.getKeyManagers();
                context.init(keyManagers, null, null);
                ssf = context.getServerSocketFactory();
                //ServerSocket ss = ssf.createServerSocket(0);
                logger.print("SSL KeyStore established...");

            } catch (Exception e) {
                logger.err("Cant Create Socket " + e.getMessage());
            }
            logger.print("");
            serverSocket = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(0);
            logger.print("");
            serverSocket.setNeedClientAuth(true);
            logger.print("");
            serverSocket.setReceiveBufferSize(LocalFile.CHUNK_SIZE);
            logger.print("");
            serverSocket.setSoTimeout(300);
            logger.print("");
            
            logger.print("");

            serverSocket.setEnabledCipherSuites(new String[]{"SSL_RSA_WITH_RC4_128_MD5"});
            logger.print("CipherSuite available: SSL_RSA_WITH_RC4_128_MD5");
            return true;
        } catch (IOException e) {
            logger.err("Unable to open new serverSocket, maybe all ports are being used: " + e.getMessage());
        }
        return false;
    }

    public boolean dead() { return socket == null; }

    public String getCoordinates() throws UnknownHostException {
        return String.format("%s:%s", InetAddress.getLocalHost().getHostAddress(), serverSocket.getLocalPort());
    }

    public byte[] receive() {
        try {
            // prepare serverSocket

            socketChecks();
            socket.close();
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
        if (socket == null) socket = serverSocket.accept();
    }


}
