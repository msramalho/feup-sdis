package src.util;

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
            char[] passphrase = "sdis18".toCharArray();
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(new FileInputStream("src/util/mykeystore/examplestore"), passphrase);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(keystore);
            context = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = tmf.getTrustManagers();
            context.init(null, trustManagers, null);

        } catch (Exception e) {
            logger.err("Cant find KeyStore file. Path may be wrong..." + e.getMessage());
        }

        try {
            
            /* SSL CIPHER CODE
            SSLSocketFactory socketFactory = context.getSocketFactory();
            sslSocket = (SSLSocket) socketFactory.createSocket(tcpCoordinates.getKey(), tcpCoordinates.getValue());
            sslSocket.setEnabledCipherSuites(new String[]{"SSL_RSA_WITH_RC4_128_MD5"});
            logger.print("CipherSuite available: SSL_RSA_WITH_RC4_128_MD5");
            sslSocket.startHandshake();
            */

            // SSL SIMPLE
            SSLSocketFactory sf = context.getSocketFactory();
            socket = sf.createSocket(tcpCoordinates.getKey(), tcpCoordinates.getValue());

        } catch (IOException e) {
            logger.err("Unable to open TCP w/ SSLSocket: " + e.getMessage());
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
