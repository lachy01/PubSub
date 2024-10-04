package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.javatuples.Pair;

public interface ConnectDirectory extends Remote {
	public Pair<String, Integer> getBroker() throws RemoteException;
	public void disconnectBroker(String IP, int port) throws RemoteException;
}
