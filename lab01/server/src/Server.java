import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 Server
 The server shall be invoked as follows:

 java Server <port_number>
 where
 <port_number> is the port number on which the server waits for requests.*
 */
public class Server {
    public static void main(String[] args) throws IOException{
        //command line parsing
        if (args.length != 1) {
            System.out.println("Usage: java Server <port_number>");
            return;
        }

        // socket and buffers set up
        DatagramSocket socket = new DatagramSocket(Integer.parseInt(args[0]));
        byte[] outData = new byte[256];
        byte[] inData = new byte[256];

        //server cycle
        while(true){
            //receive request
            DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
            socket.receive(inPacket);
            
            //process request
            String message = new String(inPacket.getData());
            System.out.println(message);

            //return response
            String response = "here is your response";
            outData = response.getBytes();
            DatagramPacket outPacket = new DatagramPacket(outData, outData.length, inPacket.getAddress(), inPacket.getPort());
            socket.send(outPacket);
        }

    }
}
