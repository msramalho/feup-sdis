package src.worker.clustering;

import src.main.Cluster;
import src.util.Message;
import src.worker.Dispatcher;

public class P_Join extends ProtocolCluster {
    public P_Join(Dispatcher d) { super(d); }

    @Override
    public void run() {
        // restart the count of peers in this cluster
        cluster.clearPeers();

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
