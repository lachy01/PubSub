package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ConnectDirectory extends Remote {
	/**
	 * Returns the RMI object reference for a single broker, the one with the fewest
	 * current connections
	 * @return The RMI object reference for the broker
	 */
	public String getBroker() throws RemoteException;
}
