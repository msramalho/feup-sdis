
package src.client;
import src.main.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestApp {
    public static void main(String args[]){
        String peerId = args[0];
        String action = args[1];
        InitiatorPeer stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            stub = (InitiatorPeer) registry.lookup(peerId);

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

        switch (action){
            case "BACKUP":
                try {
                    String file = args[2];
                    int ack = Integer.parseInt(args[3]);
                    stub.backup(file,ack);
                } catch (RemoteException e){
                    System.out.println(e.getMessage());
                }
                break;
            case "RESTORE":
                try {
                    String file = args[2];
                    stub.restore(file);
                } catch (RemoteException e){
                    System.out.println(e.getMessage());
                }
                break;
            case "DELETE":
                try {
                    String file = args[2];
                    stub.delete(file);
                } catch (RemoteException e){
                    System.out.println(e.getMessage());
                }
                break;

            case "RECLAIM":
                try {
                    int rec = Integer.parseInt(args[2]);
                    stub.reclaim(rec);
                } catch (RemoteException e){
                    System.out.println(e.getMessage());
                }
                break;

            case "STATE":
                try {
                    stub.state();
                } catch (RemoteException e){
                    System.out.println(e.getMessage());
                }
                break;

        }
    }
}

