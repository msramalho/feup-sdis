package src.main;

import src.localStorage.InternalState;
import src.util.MulticastSocketC;

import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class PeerConfig {
    public String protocolVersion;
    public Integer id; // the peer id
    InetAddress sapIp; // service access point IP
    public InetAddress machineIp = null; // Ip address of current machine, for TCP connections
    Integer sapPort; // service access point port
    public MulticastSocketC mcControl; // Multicast Control Socket
    public MulticastSocketC mcBackup; // Multicast Back Up Socket
    public MulticastSocketC mcRestore; // Multicast Restore Socket
    public ExecutorService threadPool; //global threadpool for services
    public InternalState internalState; //manager for the internal state database (non-volatile memory)

    public PeerConfig(String[] args) throws Exception {
        if (args.length < 7)
            throw new Exception("Usage: <protocolVersion> <peerId> <serviceAccessPoint> <mccIP> <mccPort> <mdbIp> <mdbPort> <mdrIp> <mdrPort>");

        threadPool = Executors.newFixedThreadPool(32);//creating a pool of 32 threads

        protocolVersion = args[0];
        id = Integer.parseInt(args[1]);
        loadServiceAccessPoint(args[2]);
        internalState = InternalState.load(id);
        readMachineIp();
        System.out.println(internalState);

        //setup sockets and join group for <mccIP> <mccPort>, <mdbIp> <mdbPort> and <mdrIp> <mdrPort>, respectively
        mcControl = new MulticastSocketC(args[3], Integer.parseInt(args[4]), id, "MCControl", this);
        mcBackup = new MulticastSocketC(args[5], Integer.parseInt(args[6]), id, "MCBackup", this);
        mcRestore = new MulticastSocketC(args[7], Integer.parseInt(args[8]), id, "MCRestore", this);
    }


    /**
     * Convert the cmd arg <serviceAccessPoint> into variables (this.sapIp, this.sapPort).
     *
     * @param sap <hostname:Port>, <IP:Port> or just <Port> in which case the IP is from localhost
     * @throws UnknownHostException when Inet4Address.getByName fails for the given IP/hostname
     */
    protected void loadServiceAccessPoint(String sap) throws UnknownHostException {
        String hostname = "localhost"; // if no other is given
        if (sap.contains(":")) {//ip and port
            String sapParts[] = sap.split(":");
            hostname = sapParts[0];
            this.sapPort = Integer.parseInt(sapParts[1]);
        } else { //port only, assume localhost
            this.sapPort = Integer.parseInt(sap);
        }
        this.sapIp = Inet4Address.getByName(hostname);
    }

    public void initialize() {
        //threads for building the requests queues
        (new Thread(this.mcControl)).start();
        (new Thread(this.mcBackup)).start();
        (new Thread(this.mcRestore)).start();
    }

    public boolean isEnhanced() { return protocolVersion != "1.0"; }

    private void readMachineIp() {
        try {
            machineIp = InetAddress.getLocalHost(); // use .getHostAddress() for the Ip string
        } catch (UnknownHostException e) {
            System.out.println("[PeerConfig] - unable to get current machine Ip address, enhanced GETCHUNK will not happen");
            e.printStackTrace();
        }
    }
}
