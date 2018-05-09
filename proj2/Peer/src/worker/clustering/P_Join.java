package src.worker.clustering;

import src.main.Cluster;
import src.util.LockException;
import src.util.Message;
import src.util.TcpServer;
import src.worker.Dispatcher;

import java.net.UnknownHostException;

/**
 * This peer receives a JOIN (only go through the global MCs so cluster should not be null)
 * If I have a cluster on the desired level I will check if there is an available slot, like my other Peers in that level
 * If I have a slot and I saw no AVAILABLE response to this, I will shut up (OPTIONAL as long as the joining peer only considers one)
 */
public class P_Join extends ProtocolCluster {
    TcpServer tcp;

    public P_Join(Dispatcher d) { super(d); }

    @Override
    public void run() throws LockException, UnknownHostException {
        if (!hasCluster()) return;

        cluster.lock("processing_join");
        cluster.clearPeers();// restart the count of peers in this cluster

        // TODO: should PRESENT include the requesting peer?
        // TODO: should PRESENT change a flag to ASSESSING so that if two peers send JOIN there isn't a cock up? especially if the peers hashset dor the cluster is reset
        // every peer sends PRESENT
        cluster.multicast.control.send(Message.create("PRESENT %s %d", d.peerConfig.protocolVersion, d.peerConfig.id));

        // sleep for 1 second (While I am asleep, the other Peer's PRESENT Messages will be counted)
        sleep(1000);

        // send AVAILABLE <version> <id> <level> <receiverId> if there is an available slot and no one silenced me
        if (!cluster.isFull() && !cluster.locked("available_silenced"))
            sendAvailable();

        cluster.unlock("processing_join");
        cluster.unlock("available_silenced");
    }

    /**
     * Join every cluster ID of the clusters above in the body of the AVAILABLE version senderId level:clusterId receiverId + body
     */
    private void sendAvailable() throws UnknownHostException {
        //TODO: simplify for
        StringBuilder upper = new StringBuilder(); // upperClustersInfo
        for (int i = d.message.level; i < d.peerConfig.clusters.size(); i++)
            upper.append(i).append(":").append(d.peerConfig.clusters.get(i).id).append(" ");

        tcp = new TcpServer();
        if (tcp.start()) {
            d.peerConfig.multicast.control.send(Message.create("AVAILABLE %s %d %d:%d %d", tcp.getCoordinates().getBytes(), d.peerConfig.protocolVersion, d.peerConfig.id, d.message.level, cluster.id, d.message.senderId));

            if (tcp.readLine().equals("ACCEPTED")) {
                tcp.sendLine(upper.toString()).close();
                cluster.peers.add(d.message.senderId);

                // if the cluster is now full and there is no upper cluster -> create an upper and see if that can join another
                if (cluster.isFull() && d.peerConfig.clusters.size() == cluster.level + 1) {
                    // create in level + 1
                    d.peerConfig.clusters.add(Cluster.getNewCluster(cluster.level + 1, d.peerConfig));
                    // join in level + 2, if any exists
                    d.peerConfig.joinCluster(cluster.level + 2, false);
                }
            }
        }

    }
}
