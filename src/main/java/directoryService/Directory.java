package directoryService;

import java.util.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import remote.BrokerDirectory;
import remote.ConnectDirectory;

public class Directory extends UnicastRemoteObject implements BrokerDirectory, ConnectDirectory {
	// List of IP, port, and number of connections for each broker
	private List<Triplet<String, Integer, Integer>> brokers;
	
	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry(1099);
			Directory directory = new Directory();
			
		}
	}
	
	protected Directory() throws RemoteException {
		brokers = new ArrayList<Triplet<String, Integer, Integer>>();
	}
	
	/**
	 * Returns a single broker for a new connection to connect to
	 * @return The IP and port number of a single broker
	 */
	@Override
	public Pair<String, Integer> getBroker() {
		Triplet<String, Integer, Integer> broker = brokers.stream().min((b1,b2) -> 
											Integer.compare(b1.getValue2(), b2.getValue2())).get();
;		return new Pair<String, Integer>(broker.getValue0(), broker.getValue1());
	}

	@Override
	public void disconnectBroker(String IP, int port) {
		// TODO Auto-generated method stub

	}

	/**
	 * Takes the IP and port associated with the new broker and returns the
	 * list of IP and ports for all other brokers
	 * @param IP address of new broker
	 * @param port number of new broker
	 * @return List of IP and port numbers for all other brokers
	 */
	@Override
	public synchronized List<Pair<String, Integer>> addBroker(String IP, int port) {
		return null;
	}

}
