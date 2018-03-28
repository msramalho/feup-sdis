package src.main;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InitiatorPeer extends Remote {

    public void backup(String file, int ack) throws RemoteException;

    public void restore(String file) throws RemoteException;

    public void delete(String file) throws RemoteException;

    public void reclaim(int rec) throws RemoteException;

    public void state() throws RemoteException;
}
