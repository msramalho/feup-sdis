package util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Custom Multicast Socket that stores the group
 */
public class MulticastSocketC extends MulticastSocket implements Runnable {
    private InetAddress group;
    private int selfId; //saves the id of the owner peer to reject own messages
    private String name;
    public LinkedBlockingDeque<Message> mcQueue; //blocking queue to store unprocessed mcControl packets
    private Runnable serviceToInvoke;
    ExecutorService threadPool;

    public MulticastSocketC(int port, InetAddress group, int selfId, String name, Runnable serviceToInvoke, ExecutorService threadPool) throws IOException {
        super(port);
        this.group = group;
        this.name = name;
        this.selfId = selfId;
        this.serviceToInvoke = serviceToInvoke;
        this.threadPool = threadPool;
        this.setTimeToLive(1);//setTimeToLeave
        this.joinGroup(this.group);
        this.mcQueue = new LinkedBlockingDeque<>(1024);
    }

    public InetAddress getGroup() {
        return this.group;
    }

    @Override
    public void run() {
        while (true) {
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
            System.out.println(new String(inPacket.getData()).trim().substring(0, 30));
            if (!m.isOwnMessage(this.selfId))
                this.mcQueue.add(m);//add this message to the blocking queue if it is not ours
        }
    }
}
