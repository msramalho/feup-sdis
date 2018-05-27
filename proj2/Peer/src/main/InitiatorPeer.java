package src.main;

import src.localStorage.InternalState;

import java.nio.file.attribute.FileTime;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InitiatorPeer extends Remote {

    void backup(String pathname, int replicationDegree) throws RemoteException;

    void restore(String pathname) throws RemoteException;

    void restoreMetadata(String fileName, String creationTime, String lastModifiedTime, long size) throws RemoteException;

    void delete(String pathname) throws RemoteException;
    
    void reclaim(int maxDiskSpace) throws RemoteException;

    void goodbye(int peer) throws RemoteException;

    void updateProtocolVersion(String newVersion) throws RemoteException;

    InternalState state() throws RemoteException;
    
    void clusterState() throws RemoteException;
}
