package main;

import java.io.IOException;

public class Peer {
    public static void main(String[] args) {

        PeerConfig peerConfig;
        try {
            peerConfig = new PeerConfig(args);//create peer
        } catch (Exception e) {
            System.out.println("[Peer] - cannot parse cmd line args");
            System.out.println(e.getMessage());
            return;
        }

        //InternalState internalState = new InternalState(peerConfig);
        //internalState.saveStorage();

        //initiator peer, receives <filename> <replicationFactor>
        if (args.length == 11) {
            System.out.println("initiator");
            String filename = args[7];
            int replicationFactor = Integer.parseInt(args[8]);

            //setup multicast socket and join group
            byte[] data = "This is the contents of the file".getBytes(); //create message with service port
            BackupChunkWorker bcWorker = new BackupChunkWorker(peerConfig, data, 1, 3);
            bcWorker.run();
            //socket.setSoTimeout(1000);//set the timeout for receive
            return;
        }

        try {
            String message = peerConfig.receiveMulticast(peerConfig.mcBackup);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }


    }
}
