package src.worker.clustering;

import src.main.Cluster;
import src.util.LockException;
import src.util.Utils;
import src.worker.Dispatcher;

/**
 * Two functions
 * 1. I sent a JOIN and I want to know if I have been accepted
 * 2. I am processing a JOIN to reply with AVAILABLE, but want to know if someone accepts this before me so I can abort
 */
public class P_Available extends ProtocolCluster {
    public P_Available(Dispatcher d) { super(d); }

    @Override
    public void run() throws LockException {
        if (isGlobal() && d.message.receiverId == d.peerConfig.id && d.peerConfig.clusters.size() <= d.message.level && d.peerConfig.lock("joining_cluster_" + d.message.level)) { // case 1
            for (String clusterId : d.message.getBodyStr().split(" ")) {
                Utils.ClusterInfo c = Utils.splitCluster(clusterId);

                Cluster newC = new Cluster(c.clusterId, c.level);
                d.peerConfig.clusters.set(c.level, newC);
                newC.loadMulticast(d.peerConfig);

                logger.print("Joined cluster " + newC.id + " at level " + newC.level);
            }
        } else if (hasCluster() && cluster.locked("processing_join")) { // case 2
            cluster.lock("available_silenced");
        }
    }
}
