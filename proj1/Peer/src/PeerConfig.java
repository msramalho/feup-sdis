import java.net.MulticastSocket;

public class PeerConfig {
    String protocolVersion;
    Integer id; // the peer id
    String sap; // service access point
    MulticastSocket mcControl; // Multicast Control Socket
    MulticastSocket mcBackUp_; // Multicast Back Up Socket
    MulticastSocket mcRestore; // Multicast Restore Socket

    public PeerConfig(String[] args) throws Exception {
        if (args.length < 7) throw new Exception("Usage: <peerId> <mccIP> <mccPort> <mdbIp> <mdbPort> <mdrIp> <mdrPort>");


    }

}
