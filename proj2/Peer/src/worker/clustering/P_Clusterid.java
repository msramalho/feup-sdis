package src.worker.clustering;

import src.worker.Dispatcher;

/**
 * Process messages "CLUSTERID version senderId level:MaxClusterID" so as to save the largest clusterId seen to use to reply to "MAXCLUSTER" messages
 */
public class P_Clusterid extends ProtocolCluster {

    public P_Clusterid(Dispatcher d) { super(d); }

    @Override
    public void run() {
        d.peerConfig.updateMaxClusterId(d.message.clusterId);
    }
}
