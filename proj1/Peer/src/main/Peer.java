package src.main;

import src.localStorage.LocalFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Peer {
    public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
        PeerConfig peerConfig;

        try {
            peerConfig = new PeerConfig(args);//create peer
        } catch (Exception e) {
            System.err.println("[Peer] - cannot parse cmd line args");
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("[Peer] - Hello, this is peer with id: " + peerConfig.id);
        peerConfig.initialize();

        //initiator peer, receives <filename> <replicationFactor>x
        if (args.length == 11) {
            // Calling LocalFile for testing
            LocalFile localFile = new LocalFile(args[9], Integer.parseInt(args[10]), peerConfig);
            localFile.splitFile();

            //sleeping and reconstructing the file
            Thread.sleep(5000); //wait for 2 seconds before sending the getchunk
            localFile.reconstructFile();
        }

    }
}
