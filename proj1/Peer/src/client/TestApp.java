package src.client;

import src.localStorage.InternalState;
import src.main.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestApp {
    public static void main(String args[]) {
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

        switch (action) {
            case "BACKUP":
                try {
                    String file = args[2];
                    int replicationDegree = Integer.parseInt(args[3]);
                    stub.backup(file, replicationDegree);
                } catch (RemoteException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "RESTORE":
                try {
                    String file = args[2];
                    stub.restore(file);
                } catch (RemoteException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "DELETE":
                try {
                    String file = args[2];
                    stub.delete(file);
                } catch (RemoteException e) {
                    System.out.println(e.getMessage());
                }
                break;

            case "RECLAIM":
                try {
                    int maxDiskSpace = Integer.parseInt(args[2]);
                    stub.reclaim(maxDiskSpace);
                } catch (RemoteException e) {
                    System.out.println(e.getMessage());
                }
                break;

            case "STATE":
                try {
                    InternalState state = stub.state();
                    System.out.println(state);
                } catch (RemoteException e) {
                    System.out.println(e.getMessage());
                }
                break;

        }
    }
}

