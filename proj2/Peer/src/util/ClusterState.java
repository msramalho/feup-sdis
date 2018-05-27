package src.util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import src.localStorage.LocalChunk;
import src.main.Cluster;
import src.main.PeerConfig;

public class ClusterState implements Runnable {

	private PeerConfig peerConfig;
	private Logger logger = new Logger(this);
	private HashMap<Cluster, ArrayList<Integer>> clusterInfo;

	public ClusterState(PeerConfig peerConfig) {
		this.peerConfig = peerConfig;
		this.clusterInfo = new HashMap<>();
	}

	@Override
	public void run() {
		TcpServer tcp = new TcpServer();
		if (tcp.start()) {
			try {
				peerConfig.multicast.control.send(Message.create("CLUSTERSTATE %s %d", tcp.getCoordinates().getBytes(), peerConfig.protocolVersion, peerConfig.id));	
				
				while(true) {
					tcp.socketAccept();
					String receivedMessage = tcp.readLine();
					saveClusterInfo(receivedMessage);
				}	
			} catch (SocketTimeoutException se) {
				logger.print("Timeout reached");
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			tcp.close();
			
			StringBuilder info = new StringBuilder();
			//CLUSTER INFO
			info.append("ClusterState{\n" + 
					"   Clusters in the Network: \n");
			for (Cluster cluster : clusterInfo.keySet()) {
				info.append(String.format("   - Cluster %3d - Level %2d", cluster.id, cluster.level)).append("\n");
			}
			
			//PEERS
			info.append("   Peers in clusters: \n");
			for (Cluster cluster : clusterInfo.keySet()) {
				info.append(String.format("   - Cluster %3d/%2d: ", cluster.id, cluster.level));
				info.append(clusterInfo.get(cluster)).append("\n");
			}
			
			//CLUSTER OCCUPATION
			info.append("   Cluster Occupation: \n");
			for (Cluster cluster : clusterInfo.keySet()) {
				info.append(String.format("   - Cluster %3d/%2d: ", cluster.id, cluster.level));
				//info.append(clusterInfo.get(cluster).size() + "/" + Cluster.MAX_SIZE 
				//		+ " = " + String.valueOf(Math.round(100 * clusterInfo.get(cluster).size() / Cluster.MAX_SIZE))).append("%\n");
				String aux = clusterInfo.get(cluster).size() + "/" + Cluster.MAX_SIZE;
				String percentage = String.valueOf(Math.round(100 * clusterInfo.get(cluster).size() / Cluster.MAX_SIZE));
				info.append(String.format("%5s %3s%%", aux, percentage)).append("\n");
			}
			info.append("}");
			
			logger.err(info.toString());
		}

	}
	
	private void saveClusterInfo(String message) {
		String[] splittedMessage = message.split("/");
		int peerId = Integer.parseInt(splittedMessage[0]);
		String[] splittedClusterInfo = splittedMessage[1].split(":");
		Cluster cluster = new Cluster(Integer.parseInt(splittedClusterInfo[0].trim()), Integer.parseInt(splittedClusterInfo[1].trim()));
		if(!clusterInfo.containsKey(cluster)) {
			clusterInfo.put(cluster, new ArrayList<Integer>(Arrays.asList(peerId)));
		}else {
			clusterInfo.get(cluster).add(peerId);
		}
		
	}
}

