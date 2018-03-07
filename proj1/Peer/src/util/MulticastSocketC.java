package util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Custom Multicast Socket that stores the group
 */
public class MulticastSocketC  extends MulticastSocket{
    private InetAddress group;

    public MulticastSocketC(int port, InetAddress group) throws IOException {
        super(port);
        this.group = group;
        this.setTimeToLive(1);//setTimeToLeave
        this.joinGroup(this.group);
    }

    public InetAddress getGroup() {
        return group;
    }
}
