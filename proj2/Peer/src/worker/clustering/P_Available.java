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

    }
}
