package src.main;


import src.localStorage.LocalFile;

public class Peer {
    public static void main(String[] args) {
        PeerConfig peerConfig;

        //TODO: local database next
        System.out.println("TODO: local database is next");

        try {
            peerConfig = new PeerConfig(args);//create peer
        } catch (Exception e) {
            System.err.println("[Peer] - cannot parse cmd line args");
            System.out.println(e.getMessage());
            return;
        }

        //InternalState internalState = new InternalState(peerConfig);
        //internalState.saveStorage();

        peerConfig.initialize();

        //initiator peer, receives <filename> <replicationFactor>x
        if (args.length == 11) {
            System.out.println("[Peer] - initiator");

            // Calling LocalFile for testing
            LocalFile localFile = new LocalFile(args[9], Integer.parseInt(args[10]), peerConfig);
            localFile.splitFile();
        }

    }
}
