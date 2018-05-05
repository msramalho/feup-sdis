package src.worker.clustering;

import src.main.Cluster;
import src.util.Message;
import src.worker.Dispatcher;

/**
 * This peer receives a JOIN (only go through the global MCs so cluster should not be null)
 * If I have a cluster on the desired level I will check if there is an available slot, like my other Peers in that level
 * If I have a slot and I saw no AVAILABLE response to this, I will shut up (OPTIONAL as long as the joining peer only considers one)
 */
public class P_Join extends ProtocolCluster {
    public P_Join(Dispatcher d) { super(d); }

    @Override
    public void run() {
        if (!hasCluster()) return;

        // restart the count of peers in this cluster
        cluster.clearPeers();
        cluster.processingJoin = true;

        // TODO: should PRESENT include the requesting peer?
        // TODO: should PRESENT change a flag to ASSESSING so that if two peers send JOIN there isn't a cock up? especially if the peers hashset dor the cluster is reset
        // every peer sends PRESENT
        cluster.multicast.control.send(Message.create("PRESENT %s %d", d.peerConfig.protocolVersion, d.peerConfig.id));

        // sleep for 1 second (While I am asleep, the other Peer's PRESENT Messages will make)
        sleep(1000);

        // send AVAILABLE <version> <id> <level> <receiverId> if there is an available slot
        //TODO: if it receives an available like (same level and same destination) the one about to be sent, it aborts
        if (cluster.peers.size() < Cluster.MAX_SIZE) {
            d.peerConfig.multicast.control.send(Message.create("AVAILABLE %s %d %d %d", d.peerConfig.protocolVersion, d.peerConfig.id, d.message.level, d.message.senderId));
        }


    }
}
