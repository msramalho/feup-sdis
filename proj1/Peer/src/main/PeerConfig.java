package src.main;


import src.util.MulticastSocketC;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerConfig {
    public String protocolVersion;
    public Integer id; // the peer id
    InetAddress sapIp; // service access point IP
    Integer sapPort; // service access point port
    public MulticastSocketC mcControl; // Multicast Control Socket
    public MulticastSocketC mcBackup; // Multicast Back Up Socket
    public MulticastSocketC mcRestore; // Multicast Restore Socket
    public ExecutorService threadPool; //global threadpool for services

    public PeerConfig(String[] args) throws Exception {
        if (args.length < 7)
            throw new Exception("Usage: <protocolVersion> <peerId> <serviceAccessPoint> <mccIP> <mccPort> <mdbIp> <mdbPort> <mdrIp> <mdrPort>");

        this.threadPool = Executors.newFixedThreadPool(16);//creating a pool of 5 threads

        this.protocolVersion = args[0];
        this.id = Integer.parseInt(args[1]);
        this.loadServiceAccessPoint(args[2]);

        //setup sockets and join group for <mccIP> <mccPort>, <mdbIp> <mdbPort> and <mdrIp> <mdrPort>, respectively
        this.mcControl = new MulticastSocketC(args[3], Integer.parseInt(args[4]), this.id, "MCControl", this.threadPool);
        this.mcBackup = new MulticastSocketC(args[5], Integer.parseInt(args[6]), this.id, "MCBackup", this.threadPool);
        this.mcRestore = new MulticastSocketC(args[7], Integer.parseInt(args[8]), this.id, "MCRestore", this.threadPool);
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

    public void initialize() {
        //threads for building the requests queues
        Thread mccThread = new Thread(this.mcControl);
        Thread mcbThread = new Thread(this.mcBackup);
        Thread mcrThread = new Thread(this.mcRestore);
        mccThread.start();
        mcbThread.start();
        mcrThread.start();
    }
}
