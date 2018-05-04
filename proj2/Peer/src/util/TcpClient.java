package src.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.AbstractMap;

public class TcpClient {
    private Logger logger = new Logger(this);

    public TcpClient() { }

    public boolean sendChunk(Message message, byte[] chunk) {
        AbstractMap.SimpleEntry<String, Integer> tcpCoordinates = message.getTCPCoordinates();
        try {
            Socket clientSocket = new Socket(tcpCoordinates.getKey(), tcpCoordinates.getValue());
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.write(chunk, 0, chunk.length);
            outToServer.flush();
            clientSocket.close(); // sends EOF
            return true;
        } catch (IOException e) {
            logger.err("Unable to connect to TCP");
            e.printStackTrace();
        }
        return false;
    }
}
