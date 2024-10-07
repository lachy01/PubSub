package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ConnectDirectory extends Remote {
	public String getBroker() throws RemoteException;
	public void disconnectBroker(String IP, int port) throws RemoteException;
}
