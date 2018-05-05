package src.worker.clustering;

import src.worker.Dispatcher;

/**
 * Two functions
 * 1. I sent a JOIN and I want to know if I have been accepted
 * 2. I am processing a JOIN to reply with AVAILABLE, but want to know if someone accepts this before me so I can abort
 */
public class P_Available extends ProtocolCluster {
    public P_Available(Dispatcher d) { super(d); }

    @Override
    public void run() {
        //TODO: extract content of if into two methods
        if (isGlobal() && d.message.receiverId == d.peerConfig.id) { // case 1

        } else if (hasCluster() && cluster.processingJoin) { // case 2
            cluster.silencedAvailable = true;
        }
    }
}
