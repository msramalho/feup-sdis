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

        this.mcControl = this.getMCGroup(args[3], args[4], () -> {
        }, "MCControl");//setup socket and join group for <mccIP> <mccPort>
        this.mcBackup = this.getMCGroup(args[5], args[6], () -> {
        }, "MCBackup");//setup socket and join group for <mdbIp> <mdbPort>
        this.mcRestore = this.getMCGroup(args[7], args[8], () -> {
        }, "MCRestore");//setup socket and join group for <mdrIp> <mdrPort>
    }

    /**
     * parse hostname/ip and port and join a multicast group saved in mcsocket
     *
     * @param hostname the hostname or IP
     * @param port     the port for the MulticastSocket
     * @throws IOException
     */
    protected MulticastSocketC getMCGroup(String hostname, String port, Runnable serviceToInvoke, String name) throws IOException {
        InetAddress mcGroupIP = Inet4Address.getByName(hostname);
        MulticastSocketC mcsocket = new MulticastSocketC(Integer.parseInt(port), mcGroupIP, this.id, name, serviceToInvoke, this.threadPool);
        //mcsocket.setTimeToLive(1);
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

    public void initialize() {
        //threads for building the requests queues
        Thread mccThread = new Thread(this.mcControl);
        Thread mcbThread = new Thread(this.mcBackup);
        Thread mcrThread = new Thread(this.mcRestore);
        mccThread.start();
        mcbThread.start();
        mcrThread.start();
    }

    /*public synchronized Message receiveMulticast(MulticastSocket mcSocket) throws IOException {
        //wait for multicast message
        //receive the response (blocking)
        byte[] responseBytes = new byte[256]; // create buffer to receive response
        DatagramPacket inPacket = new DatagramPacket(responseBytes, responseBytes.length);
        System.out.print("[PeerConfig] Waiting for multicast...");
        mcSocket.receive(inPacket);
        System.out.println("got answer: " + inPacket.getData().length + " bytes");
        return new Message(inPacket.getData());
    }*/
}
