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
					tcp.setTimeout(3000);
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
			info.append("ClusterState{\n" + 
					"   Cluster Ids: [");
			for (Cluster cluster : clusterInfo.keySet()) {
				info.append(cluster.id).append(",");
			}
			info.deleteCharAt(info.length()-2);
			info.append("]\n");
			
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

