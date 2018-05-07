package src.util;

import src.localStorage.LocalFile;

import java.io.*;
import java.net.Socket;
import java.util.AbstractMap;

public class TcpClient {
    private Logger logger = new Logger(this);
    private Socket socket;

    public TcpClient() { }

    public boolean send(Message message, byte[] chunk) {
        AbstractMap.SimpleEntry<String, Integer> tcpCoordinates = message.getTCPCoordinates();
        logger.print("making TCP available on:" + tcpCoordinates.getKey() + ":" + tcpCoordinates.getValue());
        try {
            socket = new Socket(tcpCoordinates.getKey(), tcpCoordinates.getValue());
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

    public void send(Message message, String data) {send(message, data.getBytes()); }

    public void sendLine(Message message, String data) { sendLine(message, data.getBytes()); }

    //TODO: leave only sendLine(String)
    public boolean sendLine(Message message, byte[] data) {
        AbstractMap.SimpleEntry<String, Integer> tcpCoordinates = message.getTCPCoordinates();
        logger.print("making TCP available on:" + tcpCoordinates.getKey() + ":" + tcpCoordinates.getValue());
        // assertSocket();
        try {
            socket = new Socket(tcpCoordinates.getKey(), tcpCoordinates.getValue());
            // socket.setSoTimeout(300);
            // Socket connectionSocket = socket.accept();
            DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
            // socket.close(); // sends EOF
            logger.print("sending");
            outToServer.writeBytes("mensageeeeeem" + "\n");
            // outToServer.write(data, 0, data.length);
            logger.print("sent");
            outToServer.flush();
            logger.print("flushed");
            // socket.close();
            return true;
        } catch (IOException e) {
            logger.err("Unable to connect to TCP");
            e.printStackTrace(); // TODO: remove after test
        }
        return false;
    }

    public String readLine() {
        assertSocket();
        try {
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return inFromClient.readLine();
        } catch (IOException e) {
            logger.err("Unable to read line:");
            e.printStackTrace(); // TODO: remove after test
        }
        return null;
    }

    private void assertSocket() { assert socket != null; }
}
