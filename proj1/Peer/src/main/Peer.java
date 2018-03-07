package main;

import main.PeerConfig;

import java.io.IOException;
import java.net.*;


public class Peer {


    public static void main(String[] args) {

        PeerConfig peerConfig;
        try {
            peerConfig = new PeerConfig(args);//create peer
        } catch (Exception e) {
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
            byte[] outData = "This is the message".getBytes(); //create message with service port
            try {
                DatagramPacket messsageToBroadcast = new DatagramPacket(outData, outData.length, Inet4Address.getByName(args[5]), Integer.parseInt(args[6]));
                peerConfig.mcBackup.send(messsageToBroadcast);
            } catch (Exception e) {
                System.out.println("Unable to send message");
                e.printStackTrace();
            }
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
