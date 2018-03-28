package src.main;


import src.localStorage.LocalFile;

public class Peer {
    public static void main(String[] args) {
        PeerConfig peerConfig;

        //TODO: local database next
        System.out.println("TODO: local database is next");

        try {
            peerConfig = new PeerConfig(args);//create peer
            System.out.println("[Peer] - Hello, this is peer with id: " + peerConfig.id);
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

            // Calling LocalFile for testing
            LocalFile localFile = new LocalFile(args[9], Integer.parseInt(args[10]), peerConfig);
            localFile.splitFile();

            //peerConfig.mcControl.send("GETCHUNK");
            //esperar pelo CHUNK respetivo
            // quando chegar, devolve a chunk pedida
        }

    }
}
