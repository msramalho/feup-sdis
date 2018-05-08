package src.worker.clustering;

import src.main.Cluster;
import src.worker.Dispatcher;
import src.worker.Protocol;

/**
 * Provide additional functionality that all the Protocols in clustering should have: access to a cluster
 */
abstract class ProtocolCluster extends Protocol {
    Cluster cluster = null;

    ProtocolCluster(Dispatcher d) {
        super(d);
        int level = -1;

        if (d.level != -1)  // if this is not a message on the global multicast channel
            level = d.level;
        else if (d.message.level > -1) // this message was sent on the global multicast but it is about a specific cluster
            level = d.message.level;

        // load cluster if it exists
        if (level >= 0 && level < d.peerConfig.clusters.size())
            cluster = d.peerConfig.clusters.get(level);
    }

    boolean hasCluster() { return cluster != null; }

    boolean isGlobal() { return d.level == -1; }
}
