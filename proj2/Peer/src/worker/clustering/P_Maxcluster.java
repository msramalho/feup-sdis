package src.worker.clustering;

import src.util.LockException;
import src.util.Message;
import src.worker.Dispatcher;

/**
 * Process messages "MAXCLUSTER version senderId" which essentially ask for the largest clusterId available so that the peer that asks can create a new one
 */
public class P_Maxcluster extends ProtocolCluster {

    public P_Maxcluster(Dispatcher d) { super(d); }

    @Override
    public void run() throws LockException {
        d.peerConfig.lock("max_cluster");

        int previousMaxClusterId = d.peerConfig.maxClusterId;
        d.peerConfig.maxClusterId = -1;
        sleepRandom();

        // if no one send a value greater than me, i will send mine
        if (previousMaxClusterId > d.peerConfig.maxClusterId)
            d.peerConfig.multicast.control.send(Message.create("CLUSTERID %s %d -1:%d", d.peerConfig.protocolVersion, d.peerConfig.id, previousMaxClusterId));

        // update mine, if necessary
        d.peerConfig.updateMaxClusterId(previousMaxClusterId);

        d.peerConfig.unlock("max_cluster");
    }
}
