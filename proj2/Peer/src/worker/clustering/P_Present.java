package src.worker.clustering;

import src.worker.Dispatcher;

/**
 * Adds the peerId of the sender to the hashset of my cluster at this level
 */
public class P_Present extends ProtocolCluster {
    public P_Present(Dispatcher d) { super(d); }

    @Override
    public void run() {
        //TODO : need to check if index exists? don't think so because this is always on a cluster's multicast channel
        cluster.peers.add(d.message.senderId);
    }
}
