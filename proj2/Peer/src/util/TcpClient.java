package src.util;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.AbstractMap;
import javax.net.ssl.*;
import java.security.*;
import java.net.Socket;

public class TcpClient extends Tcp {
    SSLSocket sslSocket;
    public TcpClient(Message message) {

        AbstractMap.SimpleEntry<String, Integer> tcpCoordinates = message.getTCPCoordinates();
        SSLContext context = null;

        try {
            System.setProperty("javax.net.ssl.keyStore", "src/util/ssl/client.keys");
            System.setProperty("javax.net.ssl.keyStorePassword","123456");
            System.setProperty("javax.net.ssl.trustStore", "src/util/ssl/truststore");
            System.setProperty("javax.net.ssl.trustStorePassword", "123456");

            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) sslSocketFactory.createSocket(tcpCoordinates.getKey(), tcpCoordinates.getValue());
            // socket.setEnabledCipherSuites(sslSocketFactory.getSupportedCipherSuites());
            // socket.setEnabledCipherSuites(new String[]{"SSL_RSA_WITH_RC4_128_MD5"});

        } catch (IOException e) {
            logger.err("Unable to open TCP w/ SSLSocket: " + e.getMessage());
        }
    }

    public boolean send(byte[] chunk) {
        try {
            DataOutputStream outToServer = new DataOutputStream(sslSocket.getOutputStream());
            outToServer.write(chunk, 0, chunk.length);
            outToServer.flush();
            sslSocket.close(); // sends EOF
            return true;
        } catch (IOException e) {
            logger.err("Unable to connect to TCP");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void socketChecks() {}

}
