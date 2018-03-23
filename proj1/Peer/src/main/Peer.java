package src.main;


import src.localStorage.LocalFile;

public class Peer {
    public static void main(String[] args) {
        PeerConfig peerConfig;

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
            System.out.println("initiator");

            // Calling LocalFile for testing
            LocalFile localFile = new LocalFile(args[9], Integer.parseInt(args[10]), peerConfig);
            localFile.splitFile();

            //setup multicast socket and join group
            //byte[] data = "This is the contents of the file".getBytes(); //create message with service port
            //BackupChunk bcWorker = new BackupChunk(peerConfig, data, 1, 3);
            //bcWorker.run();
            //peerConfig.threadPool.submit(bcWorker);
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //TODO: create runnable for backupService and the others so that it is listening for changes in the peerConfig.mc****.mcQueue for requests to process - use threadpool, probably peerConfig will have access to the pool so that it assigns, decide how to conjugate the blocking deques and a dispatcher of tasks, should MulticastSocket call that??
        /*try {
            Message message = peerConfig.receiveMulticast(peerConfig.mcBackup);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }*/


    }
}
