package src.main;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import src.localStorage.InternalState;
import src.localStorage.LocalFile;
import src.util.Message;

public class Peer implements InitiatorPeer {
    static LocalFile localFile;
    static PeerConfig peerConfig;

    public static void main(String[] args) {
        try {
            peerConfig = new PeerConfig(args);//create peer
        } catch (Exception e) {
            System.err.println("[Peer] - cannot parse cmd line args");
            e.printStackTrace();
            return;
        }

        try {
            Peer initPeer = new Peer();
            InitiatorPeer stub = (InitiatorPeer) UnicastRemoteObject.exportObject(initPeer, 0);
            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry("localhost");
            registry.rebind(Integer.toString(peerConfig.id), stub);
            System.err.println("[Peer] - RMI registry complete");
        } catch (Exception e) {
            System.err.println("[Peer] - RMI registry exception: " + e.toString());
            e.printStackTrace();
        }

        System.out.println(String.format("[Peer] - Hello, this is peer with id %d running version %s", peerConfig.id, peerConfig.protocolVersion));
        peerConfig.initialize();

        if (peerConfig.isEnhanced())  // only send ADELE if is enhanced
            peerConfig.mcControl.send(Message.createMessage(String.format("ADELE %s %d 0\r\n\r\n", peerConfig.protocolVersion, peerConfig.id)));

        // maintain proper state of internal database every 10s
        while (true) {
            try { Thread.sleep(10000); } catch (InterruptedException e) {}
            peerConfig.internalState.asyncChecks();
        }
    }


    @Override
    public void backup(String pathname, int replicationDegree) throws RemoteException {
        System.out.println("[Peer_RMI] - BACKUP started");
        localFile = new LocalFile(pathname, replicationDegree, peerConfig);
        localFile.backup();
    }

    @Override
    public void restore(String pathname) throws RemoteException {
        System.out.println("[Peer_RMI] - RESTORE started");
        localFile = new LocalFile(pathname, 0, peerConfig);
        try {
            localFile.reconstructFile();
        } catch (Exception e) {
            System.out.println("[Peer_RMI] - Unable to reconstruct file:");
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String pathname) throws RemoteException {
        System.out.println("[Peer_RMI] - DELETE started");
        localFile = new LocalFile(pathname, 0, peerConfig);
        localFile.deleteFile();
    }

    @Override
    public void reclaim(int maxDiskSpace) throws RemoteException {
        System.out.println("[Peer_RMI] - RECLAIM started");
        peerConfig.internalState.reclaimKBytes(maxDiskSpace);
    }

    @Override
    public InternalState state() throws RemoteException {
        System.out.println("[Peer_RMI] - STATE started");
        //TODO: complete with section 4. Client interface: Retrieve local service state information, do so by completing: addMissingInfoForClient
        InternalState is = peerConfig.internalState;
        is.addMissingInfoForClient();
        return is;
    }

    @Override
    public void updateProtocolVersion(String newVersion) throws RemoteException {
        peerConfig.protocolVersion = newVersion;
        System.out.println("[Peer_RMI] - Using version: " + newVersion);
    }

}
