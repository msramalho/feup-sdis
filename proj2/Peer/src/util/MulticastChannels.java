package src.util;

import src.main.PeerConfig;

import java.io.IOException;

public class MulticastChannels {
    public MulticastSocketC control; // Multicast Control Socket
    public MulticastSocketC backup;  // Multicast Back Up Socket
    public MulticastSocketC restore; // Multicast Restore Socket

    /**
     * explicit constructor
     */
    public MulticastChannels(PeerConfig peerConfig, String mccIp, Integer mccPort, String mcbIp, Integer mcbPort, String mcrIp, Integer mcrPort) throws IOException {
        //setup sockets and join group for <mccIP> <mccPort>, <mdbIp> <mdbPort> and <mdrIp> <mdrPort>, respectively
        control = new MulticastSocketC(mccIp, mccPort, "MCControl", peerConfig);
        backup = new MulticastSocketC(mcbIp, mcbPort, "MCBackup", peerConfig);
        restore = new MulticastSocketC(mcrIp, mcrPort, "MCRestore", peerConfig);
    }

    /**
     * Constructor from the command line arguments
     */
    public MulticastChannels(PeerConfig peerConfig, String[] args) throws IOException {
        this(peerConfig, args[3], Integer.parseInt(args[4]), args[5], Integer.parseInt(args[6]), args[7], Integer.parseInt(args[8]));
    }

    /**
     * start the listing actions on the threads
     */
    public void listen() {
        (new Thread(control)).start();
        (new Thread(backup)).start();
        (new Thread(restore)).start();
    }

    /**
     * kill the listening threads
     */
    public void stop() {
        control.stop();
        control.stop();
        control.stop();
    }
}
