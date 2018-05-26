package src.main;

import src.util.*;

import java.io.IOException;
import java.util.HashSet;

public class Cluster extends Locks {
    private static final String ADDRESS_LOWER_BOUND = "224.0.0.0";
    private static final String ADDRESS_UPPER_BOUND = "239.255.255.254";
    public static int MAX_SIZE = 2; // the maximum number of peers in a cluster
    public int id; // the unique identifier of this cluster
    public int level;
    public MulticastChannels multicast;
    public HashSet<Integer> peers = new HashSet<>();
    private Logger logger = new Logger(this);

    public Cluster(int level, int id) {
        this.level = level;
        this.id = id;
    }

    public void loadMulticast(PeerConfig peerConfig) {
        String channel = convertIdToAddress(this.id);
        try {
            multicast = new MulticastChannels(peerConfig, channel, 9000, channel, 9001, channel, 9002, level);
            multicast.listen();
        } catch (IOException e) { logger.err("Unable to listen on Multicast Channels: " + e.getMessage()); }
    }

    private String convertIdToAddress(int id) {
        long lowerBoundValue = addressToLong(ADDRESS_LOWER_BOUND);
        long upperBoundValue = addressToLong(ADDRESS_UPPER_BOUND);
        long newAddressValue = lowerBoundValue + id;
        String newAddress = intToIPAddress((int) newAddressValue);

        if (newAddressValue > upperBoundValue) {
            logger.err("Cluster Id out of bounds");
            System.exit(1);
        }

        return newAddress;
    }

    private long addressToLong(String ipAddress) {
        String[] splittedAddress = ipAddress.split("\\.");

        long result = 0;
        for (int i = 0; i < splittedAddress.length; i++) {
            int power = 3 - i;
            int ip = Integer.parseInt(splittedAddress[i]);
            result += ip * Math.pow(256, power);
        }
        return result;
    }

    private String intToIPAddress(int integer) {
        return ((integer >> 24) & 0xFF) + "."
                + ((integer >> 16) & 0xFF) + "."
                + ((integer >> 8) & 0xFF) + "."
                + (integer & 0xFF);
    }

    public void clearPeers() { peers = new HashSet<>(); }

    /**
     * query the other clusters for their ID so that a new cluster, with a new ID, can be found
     */
    public static Cluster getNewCluster(int level, PeerConfig peerConfig) {
        if (peerConfig.maxClusterId == -1) {
            peerConfig.multicast.control.send(Message.create("MAXCLUSTER %s %d", peerConfig.protocolVersion, peerConfig.id));
            Utils.sleep(1000);
        }
        return new Cluster(level, peerConfig.nextClusterId());
    }

    public boolean isFull() {
        logger.print("I HAVE " + (peers.size() + 1) + "/" + (level + 1) * Cluster.MAX_SIZE + " PEERS");
        return peers.size() + 1 >= (level + 1) * Cluster.MAX_SIZE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Cluster))
            return false;
        Cluster cluster = (Cluster) obj;
        return this.level == cluster.level && this.id == cluster.id;
    }

    @Override
    public int hashCode() {
        return level * id;
    }
}
