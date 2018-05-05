package src.worker.clustering;

import src.util.LockException;
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
        //TODO: extract content of if into two methods
        if (isGlobal() && d.message.receiverId == d.peerConfig.id) { // case 1
            //TODO: get the information in the body of the accepted so I can officially join this cluster and all the ones in the levels above

        } else if (hasCluster() && cluster.locked("processing_join")) { // case 2
            cluster.lock("available_silenced");
        }
    }
}
