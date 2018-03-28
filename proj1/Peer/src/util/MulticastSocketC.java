package src.util;

import src.main.PeerConfig;
import src.worker.Dispatcher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Custom Multicast Socket that stores the group
 */
public class MulticastSocketC extends MulticastSocket implements Runnable {
    private InetAddress group;
    private int selfId; //saves the id of the owner peer to reject own messages
    private String name;
    PeerConfig peerConfig;

    public MulticastSocketC(String hostname, int port, int selfId, String name, PeerConfig peerConfig) throws IOException {
        super(port);
        this.group = Inet4Address.getByName(hostname);
        this.name = name;
        this.selfId = selfId;
        this.peerConfig = peerConfig;
        this.setTimeToLive(1);//setTimeToLeave
        this.joinGroup(this.group);
    }

    public InetAddress getGroup() {
        return group;
    }

    public boolean send(String message) {
        byte[] data = message.getBytes();
        try {
            DatagramPacket outPacket = new DatagramPacket(data, data.length, group, getLocalPort()); // create the packet to send through the socket
            send(outPacket);
            debug(String.format("sent %5d bytes: %9s", data.length, message.split(" ", 2)[0]));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        debug("Waiting for multicast...");
        while (true) {
            //wait for multicast message + receive the response (blocking)
            byte[] responseBytes = new byte[65000]; // create buffer to receive response
            DatagramPacket inPacket = new DatagramPacket(responseBytes, responseBytes.length);
            try {
                this.receive(inPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // processed the received message: either send to queue or add task to threadpool
            Message m = new Message(inPacket.getData());
            if (!m.isOwnMessage(this.selfId)) {
                peerConfig.threadPool.submit(new Dispatcher(m, peerConfig)); // send a new task to the threadpool
                debug(String.format("received %9s from Peer %3d", m.action, m.senderId));
            }
        }
    }

    //send a message with information about which multicastsocket is displaying the message
    private void debug(String debugMessage) {
        System.out.println(String.format("[MulticastSocketC:%9s] - " + debugMessage, this.name));

    }
}
