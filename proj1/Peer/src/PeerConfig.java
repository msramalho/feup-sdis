import java.io.IOException;
import java.net.*;

public class PeerConfig {
    String protocolVersion;
    Integer id; // the peer id
    InetAddress sapIp; // service access point IP
    Integer sapPort; // service access point port
    MulticastSocket mcControl; // Multicast Control Socket
    MulticastSocket mcBackup; // Multicast Back Up Socket
    MulticastSocket mcRestore; // Multicast Restore Socket

    public PeerConfig(String[] args) throws Exception {
        if (args.length < 7)
            throw new Exception("Usage: <protocolVersion> <peerId> <serviceAccessPoint> <mccIP> <mccPort> <mdbIp> <mdbPort> <mdrIp> <mdrPort>");

        this.protocolVersion = args[0];
        this.id = Integer.parseInt(args[1]);
        this.loadServiceAccessPoint(args[2]);

        this.mcControl = PeerConfig.getMCGroup(args[3], args[4]);//setup multicast socket and join group for <mccIP> <mccPort>
        this.mcBackup = PeerConfig.getMCGroup(args[5], args[6]);//setup multicast socket and join group for <mdbIp> <mdbPort>
        this.mcRestore = PeerConfig.getMCGroup(args[7], args[8]);//setup multicast socket and join group for <mdrIp> <mdrPort>

        System.out.println(this.mcBackup);
        System.out.println("done");
    }

    /**
     * parse hostname/ip and port and join a multicast group saved in mcsocket
     * @param hostname the hostname or IP
     * @param port     the port for the MulticastSocket
     * @throws IOException
     */
    static protected MulticastSocket getMCGroup(String hostname, String port) throws IOException {
        InetAddress mcGroupIP = Inet4Address.getByName(hostname);
        MulticastSocket mcsocket = new MulticastSocket(Integer.parseInt(port));
        mcsocket.setTimeToLive(1);//setTimeToLeave
        mcsocket.joinGroup(mcGroupIP);
        return mcsocket;
    }

    /**
     * Convert the cmd arg <serviceAccessPoint> into variables (this.sapIp, this.sapPort).
     *
     * @param sap <hostname:Port>, <IP:Port> or just <Port> in which case the IP is from localhost
     * @throws UnknownHostException when Inet4Address.getByName fails for the given IP/hostname
     */
    protected void loadServiceAccessPoint(String sap) throws UnknownHostException {
        String hostname = "localhost";
        if (sap.contains(":")) {//ip and port
            String sapParts[] = sap.split(":");
            hostname = sapParts[0];
            this.sapPort = Integer.parseInt(sapParts[1]);
        } else { //port only, assume localhost
            this.sapPort = Integer.parseInt(sap);
        }
        this.sapIp = Inet4Address.getByName(hostname);
    }


    public String receiveMulticast(MulticastSocket mcSocket) throws IOException {
        //wait for multicast message
        //receive the response (blocking)
        byte[] responseBytes = new byte[256]; // create buffer to receive response
        DatagramPacket inPacket = new DatagramPacket(responseBytes, responseBytes.length);
        System.out.print("Waiting for multicast...");
        mcSocket.receive(inPacket);
        System.out.println("got answer: " + new String(inPacket.getData()));
        return new String(inPacket.getData());
    }
}




