package util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Custom Multicast Socket that stores the group
 */
public class MulticastSocketC extends MulticastSocket implements Runnable {
    private InetAddress group;
    private String name;
    private int selfId; //saves the id of the owner peer to reject own messages
    public LinkedBlockingDeque<Message> mcQueue; //blocking queue to store unprocessed mcControl packets

    public MulticastSocketC(int port, InetAddress group, int selfId) throws IOException {
        this(port, group, selfId, "ANONYMOUS");
    }

    public MulticastSocketC(int port, InetAddress group, int selfId, String name) throws IOException {
        super(port);
        this.group = group;
        this.setTimeToLive(1);//setTimeToLeave
        this.joinGroup(this.group);
        this.mcQueue = new LinkedBlockingDeque<>(1024);
        this.name = name;
        this.selfId = selfId;
    }

    public InetAddress getGroup() {
        return group;
    }

    @Override
    public void run() {
        //wait for multicast message
        //receive the response (blocking)
        byte[] responseBytes = new byte[64000]; // create buffer to receive response
        DatagramPacket inPacket = new DatagramPacket(responseBytes, responseBytes.length);
        System.out.println(String.format("[MulticastSocketC:%s] Waiting for multicast...", this.name));
        try {
            this.receive(inPacket);
            System.out.println(String.format("[MulticastSocketC:%s] answer: %d bytes", this.name, inPacket.getData().length));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Message m = new Message(inPacket.getData());
        if (!m.isOwnMessage(this.selfId)) this.mcQueue.add(m);//add this message to the blocking queue if it is not ours
    }
}
