package src.worker.clustering;

import src.main.Cluster;
import src.util.LockException;
import src.worker.Dispatcher;

import java.net.UnknownHostException;

/**
 * One of my peers has joined an upper level cluster and has asked me to join it too
 * I will do so if this is for the cluster above the one on which i receive the message
 */
public class P_Onme extends ProtocolCluster {
    public P_Onme(Dispatcher d) {super(d);}

    @Override
    public void run() throws LockException, UnknownHostException {
        if (cluster != null && cluster.level < d.message.level) {
            d.peerConfig.addCluster(new Cluster(d.message.level, d.message.clusterId));
            logger.print("ONME -> JOINED " + d.message.level + " - " + d.message.clusterId);
        }
    }
}
