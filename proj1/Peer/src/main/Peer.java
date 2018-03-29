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
            e.printStackTrace();
            return;
        }

        System.out.println("[Peer] - Hello, this is peer with id: " + peerConfig.id);
        peerConfig.initialize();

        //initiator peer, receives <filename> <replicationFactor>
        if (args.length == 11) {
            // Calling LocalFile for testing
            LocalFile localFile = new LocalFile(args[9], Integer.parseInt(args[10]), peerConfig);
            localFile.splitFile();

            //sleeping and reconstructing the file
            Thread.sleep(5000); //wait for 5 seconds before sending the getchunk
            localFile.reconstructFile();

            Thread.sleep(3000); //wait for 5 seconds before sending the getchunk
            localFile.deleteFile();
        }

        // maintain proper state of internal database every 10s
        while (true) {
            try { Thread.sleep(10000); } catch (InterruptedException e) {}
            peerConfig.internalState.asyncChecks();
        }
    }
}
