package src.main;

import src.localStorage.LocalFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements InitiatorPeer {
    public static void main(String[] args) {
        PeerConfig peerConfig;

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
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(Integer.toString(peerConfig.id), stub);
            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }


        System.out.println("[Peer] - Hello, this is peer with id: " + peerConfig.id);
        peerConfig.initialize();

        /*//initiator peer, receives <filename> <replicationFactor>
        if (args.length == 11) {
            // Calling LocalFile for testing
            LocalFile localFile = new LocalFile(args[9], Integer.parseInt(args[10]), peerConfig);
            localFile.splitFile();

            //sleeping and reconstructing the file
            // Thread.sleep(5000); //wait for 5 seconds before sending the getchunk
            // localFile.reconstructFile();

            // Thread.sleep(3000); //wait for 5 seconds before sending the getchunk
            // localFile.deleteFile();
        }*/

        // maintain proper state of internal database every 10s
        while (true) {
            try { Thread.sleep(10000); } catch (InterruptedException e) {}
            peerConfig.internalState.asyncChecks();
        }
    }

    @Override
    public void backup(String file, int ack) throws RemoteException {
        //TODO: SEND MESSAGE TO MULTICAST GROUP HERE
        System.out.println("[BACKUP]");
    }

    @Override
    public void restore(String file) throws RemoteException {
        //TODO: SEND MESSAGE TO MULTICAST GROUP HERE
        System.out.println("[RESTORE]");
    }

    @Override
    public void delete(String file) throws RemoteException {
        //TODO: SEND MESSAGE TO MULTICAST GROUP HERE
        System.out.println("[DELETE]");
    }

    @Override
    public void reclaim(int rec) throws RemoteException {
        //TODO: SEND MESSAGE TO MULTICAST GROUP HERE
        System.out.println("[RECLAIM]");
    }

    @Override
    public void state() throws RemoteException {
        //TODO: SEND MESSAGE TO MULTICAST GROUP HERE
        System.out.println("[STATE]");
    }

}
