package src.util;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class Tcp {
    Logger logger = new Logger(this);
    SSLSocket socket = null;

    public abstract void socketChecks() throws IOException;

    public Tcp sendLine(String data) {
        try {
            socketChecks();
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
            outToServer.writeBytes(data + "\n");
            outToServer.flush();
            logger.print("SENT: " + data);
        } catch (IOException e) {
            logger.err("Unable to connect to send through TCP: " + e.getMessage());
        }
        return this;
    }

    public String readLine() {
        try {
            socketChecks();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String res = inFromClient.readLine();
            logger.print("READ: " + res);
            return res;
        } catch (IOException e) {
            logger.err("Unable to read line:" + e.getMessage());
        }
        return null;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            logger.err("Unable to close TCP socket: " + e.getMessage());
        }
    }

    public static void setKeyStoreProperties() {
        System.setProperty("javax.net.ssl.keyStore", "src/util/ssl/client.keys");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
    }
}
