package src.main;

import src.localStorage.InternalState;
import src.util.Logger;
import src.util.Message;
import src.util.MulticastChannels;

import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerConfig {
    public static final String DEFAULT_VERSION = "1.0";
    public String protocolVersion;
    public Integer id; // the peer id
    public InetAddress machineIp = null; // Ip address of current machine, for TCP connections
    public MulticastChannels multicast;
    public ExecutorService threadPool; //global threadpool for services
    public InternalState internalState; //manager for the internal state database (non-volatile memory)

    private InetAddress sapIp; // service access point IP
    private Integer sapPort; // service access point port

    Logger logger = new Logger(this);

    public PeerConfig(String[] args) throws Exception {
        if (args.length < 7)
            throw new Exception("Usage: <protocolVersion> <peerId> <serviceAccessPoint> <mccIP> <mccPort> <mdbIp> <mdbPort> <mdrIp> <mdrPort>");

        threadPool = Executors.newFixedThreadPool(32);//creating a pool of 32 threads

        protocolVersion = args[0];
        id = Integer.parseInt(args[1]);
        loadServiceAccessPoint(args[2]);
        internalState = InternalState.load(id);
        readMachineIp();
        logger.print(internalState.toString());

        multicast = new MulticastChannels(this, args);
    }


    /**
     * Convert the cmd arg <serviceAccessPoint> into variables (this.sapIp, this.sapPort).
     *
     * @param sap <hostname:Port>, <IP:Port> or just <Port> in which case the IP is from localhost
     * @throws UnknownHostException when Inet4Address.getByName fails for the given IP/hostname
     */
    private void loadServiceAccessPoint(String sap) throws UnknownHostException {
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

    public boolean isEnhanced() { return !protocolVersion.equals(PeerConfig.DEFAULT_VERSION); }

    public static boolean isMessageEnhanced(Message m) { return !m.protocolVersion.equals(PeerConfig.DEFAULT_VERSION); }

    private void readMachineIp() {
        try {
            machineIp = InetAddress.getLocalHost(); // use .getHostAddress() for the Ip string
        } catch (UnknownHostException e) {
            logger.print("unable to get current machine Ip address, enhanced GETCHUNK will not happen");
            e.printStackTrace();
        }
    }
}
