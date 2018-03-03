public class Peer {
    static PeerConfig peerConfig;

    public static void main(String[] args) {
        try {
            //create peer
            peerConfig = new PeerConfig(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        //initiator peer, receives <filename> <replicationFactor>
        if (args.length == 11) {
            System.out.println("initiator");
            String filename = args[7];
            int replicationFactor = Integer.parseInt(args[8]);
        }


    }
}
