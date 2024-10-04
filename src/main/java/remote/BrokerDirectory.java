package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.javatuples.Pair;

public interface BrokerDirectory extends Remote {
	public List<Pair<String, Integer>> addBroker(String IP, int port) throws RemoteException;
}
