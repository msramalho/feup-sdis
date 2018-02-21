import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.util.Arrays;

/**
 *
 Client
 java client <mcast_addr> <mcast_port> <oper> <opnd> *
 where:
 <mcast_addr> is the IP address of the multicast group used by the server to advertise its service;
 <mcast_port> is the port number of the multicast group used by the server to advertise its service;
 <oper> is ''register'' or ''lookup'', depending on the operation to invoke;
 <opnd> * is the list of operands of the specified operation:
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

        //setup multicast socket and join group
        MulticastSocket mcSocket = new MulticastSocket(Integer.parseInt(args[1]));
        InetAddress mcGroupIP = Inet4Address.getByName(args[0]);
        mcSocket.joinGroup(mcGroupIP);

        //wait for multicast message
        //receive the response (blocking)
        byte[] responseBytes =  new byte[256]; // create buffer to receive response
        DatagramPacket inPacket = new DatagramPacket(responseBytes, responseBytes.length);
        System.out.print("Waiting for broadcast...");
        mcSocket.receive(inPacket);
        //leave multicast group
        mcSocket.leaveGroup(mcGroupIP);
        System.out.println("got answer: " + new String(inPacket.getData()));



        int port = Integer.parseInt((new String(inPacket.getData())).trim()); // use the specified port
        String mergedCommands = args[2] + " " +  String.join(" ", Arrays.asList(args).subList(3, args.length)); // merge <opnd>* into a string to send the server
        System.out.print(mergedCommands); // debug
        byte[] data = mergedCommands.getBytes(); // get the data to send into a byte array

        //socket set up
        InetAddress ip4 = inPacket.getAddress();// Inet4Address.getByName(args[0]); // DNS from host_name to InetAddress (from ipv4)
        DatagramSocket socket = new DatagramSocket(); // create socket for UDP Unicast
        DatagramPacket outPacket = new DatagramPacket(data, data.length, ip4, port); // create the packet to send through the socket

        //send data
        socket.send(outPacket); // send the packet

        //receive the response (blocking)
        responseBytes =  new byte[256]; // empty the receive buffer
        inPacket = new DatagramPacket(responseBytes, responseBytes.length);
        socket.receive(inPacket);
        String response = new String(inPacket.getData());
        System.out.println(" : " + response); // debug

    }
}
