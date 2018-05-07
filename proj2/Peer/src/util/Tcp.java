package src.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public abstract class Tcp {
    Logger logger = new Logger(this);
    Socket socket = null;

    public abstract void socketChecks() throws IOException;

    public boolean sendLine(String data) {
        try {
            socketChecks();
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
            outToServer.writeBytes(data + "\n");
            outToServer.flush();
            logger.print("SENT: " + data);
            return true;
        } catch (IOException e) {
            logger.err("Unable to connect to send through TCP: " + e.getMessage());
        }
        return false;
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
}
