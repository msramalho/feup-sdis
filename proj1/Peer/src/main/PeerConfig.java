package main;

import util.MulticastSocketC;
import util.Message;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class PeerConfig {
    String protocolVersion;
    Integer id; // the peer id
    InetAddress sapIp; // service access point IP
    Integer sapPort; // service access point port
    MulticastSocketC mcControl; // Multicast Control Socket
    MulticastSocketC mcBackup; // Multicast Back Up Socket
    MulticastSocketC mcRestore; // Multicast Restore Socket
    BlockingQueue mcQueue; //blocking queue to store unprocessed mcControl packets

    public PeerConfig(String[] args) throws Exception {
        if (args.length < 7)
            throw new Exception("Usage: <protocolVersion> <peerId> <serviceAccessPoint> <mccIP> <mccPort> <mdbIp> <mdbPort> <mdrIp> <mdrPort>");

        this.protocolVersion = args[0];
        this.id = Integer.parseInt(args[1]);
        this.loadServiceAccessPoint(args[2]);

        this.mcControl = PeerConfig.getMCGroup(args[3], args[4]);//setup multicast socket and join group for <mccIP> <mccPort>
        this.mcBackup = PeerConfig.getMCGroup(args[5], args[6]);//setup multicast socket and join group for <mdbIp> <mdbPort>
        this.mcRestore = PeerConfig.getMCGroup(args[7], args[8]);//setup multicast socket and join group for <mdrIp> <mdrPort>

        this.mcQueue = new ArrayBlockingQueue(1024);
    }

    /**
     * parse hostname/ip and port and join a multicast group saved in mcsocket
     * @param hostname the hostname or IP
     * @param port     the port for the MulticastSocket
     * @throws IOException
     */
    static protected MulticastSocketC getMCGroup(String hostname, String port) throws IOException {
        InetAddress mcGroupIP = Inet4Address.getByName(hostname);
        MulticastSocketC mcsocket = new MulticastSocketC(Integer.parseInt(port), mcGroupIP);
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


    public synchronized Message receiveMulticast(MulticastSocket mcSocket) throws IOException {
        //wait for multicast message
        //receive the response (blocking)
        byte[] responseBytes = new byte[256]; // create buffer to receive response
        DatagramPacket inPacket = new DatagramPacket(responseBytes, responseBytes.length);
        System.out.print("[PeerConfig] Waiting for multicast...");
        mcSocket.receive(inPacket);
        System.out.println("got answer: " + inPacket.getData().length + " bytes");
        return new Message(inPacket.getData());
    }
}




