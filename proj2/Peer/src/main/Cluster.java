package src.main;

import src.util.MulticastChannels;

import java.util.HashSet;

public class Cluster {
    public static int MAX_SIZE = 4; // the maximum number of peers in a cluster
    int id; // the unique identifier of this cluster
    // TODO: decide if useful: int level; // the level at which it is located (starts at 0) - can be inferred from the clusters arraylist, but this is useful
    public MulticastChannels multicast;
    public HashSet<Integer> peers;

    public void clearPeers() {
        peers = new HashSet<>();
    }
}
