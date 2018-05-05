package src.worker.clustering;

import src.main.Cluster;
import src.worker.Dispatcher;
import src.worker.Protocol;

/**
 * Provide additional functionality that all the Protocols in clustering should have: access to a cluster
 */
public abstract class ProtocolCluster extends Protocol {
    Cluster cluster;

    public ProtocolCluster(Dispatcher d) {
        super(d);
        if (d.depth != -1) { // if this is not a message on the global multicast channel
            cluster = d.peerConfig.clusters.get(d.depth);
        } else if (d.message.level >= -1 && d.message.level < d.peerConfig.clusters.size()) { // this message was sent on the global multicast but it is about a specific cluster
            cluster = d.peerConfig.clusters.get(d.message.level);
        }
    }
}
