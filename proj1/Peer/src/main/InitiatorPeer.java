package src.main;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InitiatorPeer extends Remote {

    void backup(String file, int ack) throws RemoteException;

    void restore(String file) throws RemoteException;

    void delete(String file) throws RemoteException;

    void reclaim(int rec) throws RemoteException;

    void state() throws RemoteException;
}
