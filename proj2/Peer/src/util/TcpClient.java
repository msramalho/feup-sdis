package src.util;

import java.io.*;
import java.util.AbstractMap;
import javax.net.ssl.*;
import java.security.*;

public class TcpClient extends Tcp {
    public TcpClient(Message message) {

        AbstractMap.SimpleEntry<String, Integer> tcpCoordinates = message.getTCPCoordinates();

        SSLSocketFactory sf = null;

        try {
            char[] passphrase = "sdis18".toCharArray();
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(new FileInputStream("src/util/mykeystore/examplestore"), passphrase);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(keystore);
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManager[] trustManagers = tmf.getTrustManagers();
            context.init(null, trustManagers, null);
            sf = context.getSocketFactory();

        } catch (Exception e) {
            logger.err("[KEY STORE] - Cant find KeyStore file. Path may be wrong..." + e.getMessage());
        }

        try {
            socket = sf.createSocket(tcpCoordinates.getKey(), tcpCoordinates.getValue());
            System.out.println("[TCP SSLClient] - SSL Connected");
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
            logger.err("Unable to connect to SSLSocket");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void socketChecks() {}

}
