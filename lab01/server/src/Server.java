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
        byte[] outData;
        byte[] inData = new byte[256];
        DataBase db = new DataBase();
        DatagramPacket inPacket = new DatagramPacket(inData, inData.length);

        //server cycle
        while(true){
            //receive request
            inData = new byte[inData.length];
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
        }
    }

    //some extra validation could be done in this function
    public static String executeCommand(String command, DataBase db) throws Exception {
        //parse command
        System.out.print("Command received: " + command); // debug
        String[] parts = command.trim().split(" ");
        for (String part: parts) //trim extra-whitespaces
            part = part.trim();

        if (parts.length == 3 && parts[0].toUpperCase().equals("REGISTER"))
            return db.register(new LicensePlate(parts[1], parts[2])).toString();
        else if (parts.length == 2 && parts[0].toUpperCase().equals("LOOKUP"))
            return db.lookup(parts[1]);

        return "COMMAND NOT FOUND";
    }
}
