package src.main;

import src.util.Locks;
import src.util.MulticastChannels;

import java.util.HashSet;

public class Cluster extends Locks {
    public static int MAX_SIZE = 4; // the maximum number of peers in a cluster
    int id; // the unique identifier of this cluster
    public MulticastChannels multicast;
    public HashSet<Integer> peers;

    public void clearPeers() { peers = new HashSet<>(); }
}
