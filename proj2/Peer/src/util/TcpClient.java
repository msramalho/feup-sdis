package src.util;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.util.AbstractMap;

public class TcpClient extends Tcp {
    public TcpClient(Message message) {
        AbstractMap.SimpleEntry<String, Integer> tcpCoordinates = message.getTCPCoordinates();
        try {
            System.setProperty("javax.net.ssl.trustStore", "src/util/ssl/truststore");
            System.setProperty("javax.net.ssl.trustStorePassword", "123456");
            System.setProperty("javax.net.ssl.keyStore", "src/util/ssl/client.keys");
            System.setProperty("javax.net.ssl.keyStorePassword","123456");
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) sslSocketFactory.createSocket(tcpCoordinates.getKey(), tcpCoordinates.getValue());
            socket.setEnabledCipherSuites(sslSocketFactory.getSupportedCipherSuites());
        } catch (IOException e) {
            logger.err("Unable to open TCP socket: " + e.getMessage());
        }
    }

    public boolean send(byte[] chunk) {
        try {
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
            outToServer.write(chunk, 0, chunk.length);
            outToServer.flush();
            socket.close(); // sends EOF
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
