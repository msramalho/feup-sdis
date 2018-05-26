package src.client;

import src.localStorage.InternalState;
import src.main.*;
import src.util.Logger;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestApp {
    private static Logger logger = new Logger("TestApp");
    public static void main(String args[]) {
        String peerId = args[0];
        String action = args[1];
        InitiatorPeer stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry("localhost");
            stub = (InitiatorPeer) registry.lookup(peerId);

        } catch (Exception e) {
            logger.err("Client exception: " + e.toString());
            e.printStackTrace();
        }

        //change the version according to the usage of enhancements
        String version = "1.0";
        String enhancedVersion = "1.1";
        if (action.substring(action.length() - 3).equals("ENH")) version = enhancedVersion;
        try {
            assert stub != null;
            stub.updateProtocolVersion(version); // send the correct protocol version
        } catch (RemoteException e) {
            logger.err(e.getMessage());
        }

        switch (action) {
            case "BACKUPENH":
            case "BACKUP":
                try {
                    String file = args[2];
                    int replicationDegree = Integer.parseInt(args[3]);
                    stub.backup(file, replicationDegree);
                } catch (RemoteException e) {
                    logger.err(e.getMessage());
                }
                break;
            case "RESTOREENH":
            case "RESTORE":
                try {
                    String file = args[2];
                    stub.restore(file);
                } catch (RemoteException e) {
                    logger.err(e.getMessage());
                }
                break;
            case "RESTOREMETADATA":
                try {
                    String file = args[2]; // falta os outros argumentos
                    String creationTme = args[3];
                    String lastModifiedTime = args[4];
                    long size = Long.parseLong(args[5]);
                    stub.restoreMetadata(file, creationTme, lastModifiedTime, size);
                } catch (RemoteException e) {
                    logger.err(e.getMessage());
                }
                break;
            case "DELETEENH":
            case "DELETE":
                try {
                    String file = args[2];
                    stub.delete(file);
                } catch (RemoteException e) {
                    logger.err(e.getMessage());
                }
                break;
            case "RECLAIMENH":
            case "RECLAIM":
                try {
                    int maxDiskSpace = Integer.parseInt(args[2]);
                    stub.reclaim(maxDiskSpace);
                } catch (RemoteException e) {
                    logger.err(e.getMessage());
                }
                break;
            case "STATEENH":
            case "STATE":
                try {
                    InternalState state = stub.state();
                    logger.err(state.toString());
                } catch (RemoteException e) {
                    logger.err(e.getMessage());
                }
                break;
            case "CLUSTERSTATE":
                try {
                    stub.clusterState();
                } catch (RemoteException e) {
                    logger.err(e.getMessage());
                }
                break;
        }
    }
}

