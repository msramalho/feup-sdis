package src.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import src.localStorage.InternalState;
import src.localStorage.LocalFile;
import src.util.Logger;
import src.util.Message;

public class Peer implements InitiatorPeer {
    private static LocalFile localFile;
    private static PeerConfig peerConfig;
    static Logger logger = new Logger("Peer");

    public static void main(String[] args) {
        try {
            peerConfig = new PeerConfig(args);//create peer
        } catch (Exception e) {
            logger.err("cannot parse cmd line args");
            e.printStackTrace();
            return;
        }

        try {
            Peer initPeer = new Peer();
            InitiatorPeer stub = (InitiatorPeer) UnicastRemoteObject.exportObject(initPeer, 0);
            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry("localhost");
            registry.rebind(Integer.toString(peerConfig.id), stub);
            logger.print("RMI registry complete");
        } catch (Exception e) {
            logger.err("RMI registry exception: " + e.toString());
            e.printStackTrace();
        }

        logger.print(String.format("Hello, this is peer with id %d running version %s", peerConfig.id, peerConfig.protocolVersion));
        
        peerConfig.multicast.listen();
        peerConfig.joinCluster();

        if (peerConfig.isEnhanced())  // only send HELLO if is enhanced
            peerConfig.multicast.control.send(Message.create("HELLO %s %d 0\r\n\r\n", peerConfig.protocolVersion, peerConfig.id));

        // maintain proper state of internal database every 10s
        while (true) {
            try { Thread.sleep(10000); } catch (InterruptedException ignored) {}
            peerConfig.internalState.asyncChecks();
        }
    }


    @Override
    public void backup(String pathname, int replicationDegree) {
        logger.print("BACKUP started");
        localFile = new LocalFile(pathname, replicationDegree, peerConfig);
        localFile.backup();
    }

    @Override
    public void restore(String pathname) {
        logger.print("RESTORE started");
        localFile = new LocalFile(pathname, 0, peerConfig);
        try {
            localFile.reconstructFile();
        } catch (Exception e) {
            logger.print("Unable to reconstruct file:");
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String pathname) {
        logger.print("DELETE started");
        localFile = new LocalFile(pathname, 0, peerConfig);
        localFile.deleteFile();
    }

    @Override
    public void reclaim(int maxDiskSpace) {
        logger.print("RECLAIM started");
        peerConfig.internalState.reclaimKBytes(maxDiskSpace);
    }

    @Override
    public InternalState state() {
        logger.print("STATE started");
        //TODO: complete with section 4. Client interface: Retrieve local service state information, do so by completing: addMissingInfoForClient
        InternalState is = peerConfig.internalState;
        is.addMissingInfoForClient();
        return is;
    }

    @Override
    public void updateProtocolVersion(String newVersion) {
        peerConfig.protocolVersion = newVersion;
        logger.print("Using version: " + newVersion);
    }

}
