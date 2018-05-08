package src.main;

import src.util.*;

import java.io.IOException;
import java.util.HashSet;

public class Cluster extends Locks {
    public static int MAX_SIZE = 2; // the maximum number of peers in a cluster
    public int id; // the unique identifier of this cluster
    public int level;
    public MulticastChannels multicast;
    public HashSet<Integer> peers;
    private Logger logger = new Logger(this);

    public Cluster(int level, int id) {
        this.level = level;
        this.id = id;
    }

    public void loadMulticast(PeerConfig peerConfig) {
        //TODO deterministic function to get valid IP Multicast from the id
        String channel = "225.0.0.2";
        try {
            multicast = new MulticastChannels(peerConfig, channel, 9000, channel, 9001, channel, 9002, level);
            multicast.listen();
        } catch (IOException e) { logger.err("Unable to listen on Multicast Channels: " + e.getMessage()); }
    }

    public void clearPeers() { peers = new HashSet<>(); }
}
