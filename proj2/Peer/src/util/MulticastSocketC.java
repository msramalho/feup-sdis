package src.util;

import src.main.PeerConfig;
import src.worker.Dispatcher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Custom Multicast Socket that stores the group
 */
public class MulticastSocketC extends MulticastSocket implements Runnable {
    private InetAddress group;
    private PeerConfig peerConfig;
    private Logger logger;
    private boolean active = true;
    private int level; // the level of the cluster for this MC. The global has -1

    public MulticastSocketC(String hostname, int port, String name, PeerConfig peerConfig, int level) throws IOException {
        super(port);
        this.group = Inet4Address.getByName(hostname);
        this.peerConfig = peerConfig;
        this.level = level;
        this.setTimeToLive(1);
        this.joinGroup(this.group);
        logger = new Logger(this, name);
    }

    public boolean send(byte[] data) {
        try {
            DatagramPacket outPacket = new DatagramPacket(data, data.length, group, getLocalPort()); // create the packet to send through the socket
            send(outPacket);
            logger.print(String.format("sent %5d bytes in: %s", data.length, new String(data).split(" ", 2)[0]));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void stop() { this.active = false; }

    @Override
    public void run() {
        logger.print("Waiting for multicast...");
        while (active) {
            //wait for multicast message + receive the response (blocking)
            byte[] responseBytes = new byte[65507]; // create buffer to receive response, max UDP Datagram size
            DatagramPacket inPacket = new DatagramPacket(responseBytes, responseBytes.length);

            try {
                this.receive(inPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // processed the received message: either send to queue or add task to threadpool
            Message m = new Message(inPacket);
            if (!m.isOwnMessage(peerConfig.id)) { // reject own messages
                peerConfig.threadPool.submit(new Dispatcher(m, peerConfig, level)); // send a new task to the threadpool
                logger.print(String.format("received %s from Peer %d (%d bytes in body)", m.header, m.senderId, m.body.length));
            }
        }
    }
}
