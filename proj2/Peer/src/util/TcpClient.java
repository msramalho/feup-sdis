package src.util;

import java.io.*;
import java.net.Socket;
import java.util.AbstractMap;

public class TcpClient extends Tcp {
    public TcpClient(Message message) {
        AbstractMap.SimpleEntry<String, Integer> tcpCoordinates = message.getTCPCoordinates();
        try {
            socket = new Socket(tcpCoordinates.getKey(), tcpCoordinates.getValue());
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
