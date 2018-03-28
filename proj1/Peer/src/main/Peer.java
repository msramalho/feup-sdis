package src.main;


import src.localStorage.LocalFile;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Peer implements InitiatorPeer {
    public static void main(String[] args) {
        PeerConfig peerConfig = null;

        try {
            peerConfig = new PeerConfig(args);//create peer
        } catch (Exception e) {
            e.printStackTrace();
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

        //TODO: local database next
        System.out.println("TODO: local database is next");


        /*try {
            peerConfig = new PeerConfig(args);//create peer
        } catch (Exception e) {
            System.err.println("[Peer] - cannot parse cmd line args");
            System.out.println(e.getMessage());
            return;

        //InternalState internalState = new InternalState(peerConfig);
        //internalState.saveStorage();

        peerConfig.initialize();

        //initiator peer, receives <filename> <replicationFactor>x
        if (args.length == 11) {

            // Calling LocalFile for testing
            LocalFile localFile = new LocalFile(args[9], Integer.parseInt(args[10]), peerConfig);
            localFile.splitFile();

            //peerConfig.mcControl.send("GETCHUNK");
            //esperar pelo CHUNK respetivo
            // quando chegar, devolve a chunk pedida
        }

    }*/

    }
        @Override
        public void backup(String file, int ack)  throws RemoteException {
            //TODO: SEND MESSAGE TO MULTICAST GROUP HERE
            System.out.println("[BACKUP]");
        }

        @Override
        public void restore(String file) throws RemoteException {
            //TODO: SEND MESSAGE TO MULTICAST GROUP HERE
            System.out.println("[RESTORE]");
        }

        @Override
        public void delete(String file)  throws RemoteException {
            //TODO: SEND MESSAGE TO MULTICAST GROUP HERE
            System.out.println("[DELETE]");
        }

        @Override
        public void reclaim(int rec) throws RemoteException {
            //TODO: SEND MESSAGE TO MULTICAST GROUP HERE
            System.out.println("[RECLAIM]");
        }

        @Override
        public void state () throws RemoteException {
            //TODO: SEND MESSAGE TO MULTICAST GROUP HERE
            System.out.println("[STATE]");
        }

}
