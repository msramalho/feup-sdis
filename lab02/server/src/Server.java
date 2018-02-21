import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 java Server <srvc_port> <mcast_addr> <mcast_port>
 where:
 <srvc_port> is the port number where the server provides the service
 <mcast_addr> is the IP address of the multicast group used by the server to advertise its service.
 <mcast_port> is the multicast group port number used by the server to advertise its service.
 */
public class Server {
    public static void main(String[] args) throws IOException{
        //command line parsing
        if (args.length != 3) {
            System.out.println("Usage:  java Server <srvc_port> <mcast_addr> <mcast_port>");
            return;
        }

        //setup multicast socket and join group
        MulticastSocket mcSocket = new MulticastSocket();
        mcSocket.setTimeToLive(1);//setTimeToLeave is the only reason we use MulticastSocket, if not needed, a common socket would suffice
        InetAddress mcGroupIP = Inet4Address.getByName(args[1]);
        byte[] outData = args[0].getBytes(); //create message with service port
        DatagramPacket messsageToBroadcast = new DatagramPacket(outData, outData.length, mcGroupIP, Integer.parseInt(args[2]));


        // socket and buffers set up
        DatagramSocket socket = new DatagramSocket(Integer.parseInt(args[0]));
        socket.setSoTimeout(1000);//set the timeout for receive
        byte[] inData;
        DataBase db = new DataBase();

        //server cycle
        while(true){

            //send multicast message
            System.out.println("Multicasting...");
            mcSocket.send(messsageToBroadcast);

            //receive request (execute the licenseplate service)
            inData = new byte[256];
            DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
            try {
                socket.receive(inPacket);
                //process request and return response (handles exceptions)
                String response;
                try {
                    response = executeCommand(new String(inPacket.getData()), db);
                } catch (Exception e){
                    response = e.getMessage();
                }

                System.out.println(" : " + response); // debug
                outData = response.getBytes();
                DatagramPacket outPacket = new DatagramPacket(outData, outData.length, inPacket.getAddress(), inPacket.getPort());
                socket.send(outPacket);
            }catch (SocketTimeoutException e){
                System.out.println("Got Timeout");
            }

        }




    }

    //some extra validation could be done in this function
    public static String executeCommand(String command, DataBase db) throws Exception {
        //parse command
        command = command.trim();
        System.out.print(command); // debug
        String[] parts = command.split(" ");
        for (String part: parts) //trim extra-whitespaces
            part = part.trim();

        if (parts.length >= 3 && parts[0].toUpperCase().equals("REGISTER"))
            return db.register(new LicensePlate(parts[1], String.join(" ", Arrays.asList(parts).subList(2, parts.length)))).toString();
        else if (parts.length == 2 && parts[0].toUpperCase().equals("LOOKUP"))
            return db.lookup(parts[1]);

        return "COMMAND NOT FOUND";
    }
}
