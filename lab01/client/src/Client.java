import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.Inet4Address;
import java.net.InetAddress;

/**
 *
 Client
 The client must be invoked as follows:

 java Client <host_name> <port_number> <oper> <opnd>*
 where
 <host_name> is the name of the host running the server;
 <port_number> is the server port;
 <oper> is either ‘‘register’’or ‘‘lookup’’
 <opnd>* is the list of arguments
 <plate number> <owner name>, for register;
 <plate number>, for lookup.
 */
public class Client {
    public static void main(String[] args) throws IOException {
        //command line parsing
        if (args.length < 4){
            System.out.println("Usage: java Client <host_name> <port_number> <oper> <opnd>*");
            return;
        }
        int port = Integer.parseInt(args[1]); // use the specified port
        byte[] data = args[2].getBytes(); // get the data to send into a byte array

        //socket set up
        InetAddress ip4 = Inet4Address.getByName(args[0]); // DNS from host_name to InetAddress (from ipv4)
        DatagramSocket socket = new DatagramSocket(); // create socket for UDP Unicast
        DatagramPacket outPacket = new DatagramPacket(data, data.length, ip4, port); // create the packet to send through the socket

        //send data
        socket.send(outPacket); // send the packet

        //receive the response (blocking)
        byte[] responseBytes =  new byte[256]; // create buffer to receive response
        DatagramPacket inPacket = new DatagramPacket(responseBytes, responseBytes.length);
        socket.receive(inPacket);
        String response = new String(inPacket.getData());
        System.out.println("Reply: " + response);
    }
}
