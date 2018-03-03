import java.net.*;

public class PeerConfig {
    String protocolVersion;
    Integer id; // the peer id
    InetAddress sapIp; // service access point IP
    Integer sapPort; // service access point port
    MulticastSocket mcControl; // Multicast Control Socket
    MulticastSocket mcBackUp_; // Multicast Back Up Socket
    MulticastSocket mcRestore; // Multicast Restore Socket

    public PeerConfig(String[] args) throws Exception {
        if (args.length < 7)
            throw new Exception("Usage: <protocolVersion> <peerId> <serviceAccessPoint> <mccIP> <mccPort> <mdbIp> <mdbPort> <mdrIp> <mdrPort>");

        this.protocolVersion = args[0];
        this.id = Integer.parseInt(args[1]);
        this.loadServiceAccessPoint(args[2]);

        //setup multicast socket and join group for <mccIP> <mccPort>
        InetAddress mccGroupIP = Inet4Address.getByName(args[3]); //<mccIP>
        this.mcControl = new MulticastSocket(Integer.parseInt(args[4])); // <mccPort>
        this.mcControl.joinGroup(mccGroupIP);

        //setup multicast socket and join group for <mdbIp> <mdbPort>
        InetAddress mdbGroupIP = Inet4Address.getByName(args[5]); //<mdbIp>
        this.mcBackUp_ = new MulticastSocket(Integer.parseInt(args[6])); // <mdbPort>
        this.mcBackUp_.joinGroup(mdbGroupIP);

        //setup multicast socket and join group for <mdrIp> <mdrPort>
        InetAddress mdrGroup = Inet4Address.getByName(args[7]); //<mdrIp>
        this.mcRestore = new MulticastSocket(Integer.parseInt(args[8])); // <mdrPort>
        this.mcRestore.joinGroup(mdrGroup);
        System.out.println("done");
    }

    /**
     * Convert the cmd arg <serviceAccessPoint> into variables (this.sapIp, this.sapPort).
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

}




