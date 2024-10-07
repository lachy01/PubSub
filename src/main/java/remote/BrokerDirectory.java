package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BrokerDirectory extends Remote {
	/**
	 * Adds a broker to the broker network
	 * @param brokerObjRef The new broker's object reference
	 * @return the list of broker object references currently in the network
	 * @throws RemoteException
	 */
	public List<String> addBroker(String brokerObjRef) throws RemoteException;
}
