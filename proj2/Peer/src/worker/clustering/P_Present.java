package src.worker.clustering;

import src.worker.Dispatcher;
import src.worker.Protocol;

/**
 * Adds the peerId of the sender to the hashset of the cluster at this level
 */
public class P_Present extends Protocol {
    public P_Present(Dispatcher d) {
        super(d);
    }

    @Override
    public void run() {
        //TODO : need to check if index exists? don't think so
        d.peerConfig.clusters.get(d.depth).peers.add(d.message.senderId);
    }
}
