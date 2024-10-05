package directoryService;

import java.util.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import remote.BrokerDirectory;
import remote.ConnectDirectory;

public class Directory extends UnicastRemoteObject implements BrokerDirectory, ConnectDirectory {
	// List of IP, port, and number of connections for each broker
	private List<Triplet<String, Integer, Integer>> brokers;
	private static Registry registry;
	
	public static void main(String[] args) {
		try {
			int portNum = Integer.parseInt(args[0]);
			registry = LocateRegistry.createRegistry(portNum);
			Directory directory = new Directory();
			registry.rebind("Directory", directory);
		}
		catch(Exception e) {
			
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
		broker.setAt2(broker.getValue2() + 1);
;		return new Pair<String, Integer>(broker.getValue0(), broker.getValue1());
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
		brokers.add(new Triplet<String, Integer, Integer>(IP, port, 0));
		
		return brokers.subList(0, brokers.size()).stream().
				map(x -> new Pair<String, Integer>(x.getValue0(), x.getValue1()))
				.collect(Collectors.toList());
	}
	
	
	@Override
	public void disconnectBroker(String IP, int port) {
		// TODO Auto-generated method stub

	}
}
