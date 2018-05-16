package src.worker.clustering;

import java.net.UnknownHostException;
import java.util.Iterator;

import src.localStorage.InternalState;
import src.main.Cluster;
import src.util.LockException;
import src.util.Message;
import src.util.TcpClient;
import src.util.Utils;
import src.worker.Dispatcher;

public class P_Clusterstate extends ProtocolCluster {

	public P_Clusterstate(Dispatcher d) { super(d); }
	
	@Override
	public void run() throws LockException, UnknownHostException {
		
		sleepRandom();
		TcpClient tcp = new TcpClient(d.message);
		
		StringBuilder upper = new StringBuilder(); // upperClustersInfo		
		for (int i = 0; i < d.peerConfig.clusters.size(); i++) {
			upper.append(d.peerConfig.clusters.get(i).level + ":" + d.peerConfig.clusters.get(i).id).append(" ");
			
		}	
		tcp.sendLine(String.format("%d/%s", d.peerConfig.id, upper.toString()));		

		tcp.close();		
		
	}

}
