package directoryService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import remote.BrokerDirectory;
import remote.ConnectDirectory;

public class Directory extends UnicastRemoteObject implements BrokerDirectory, ConnectDirectory {
	private static final long serialVersionUID = 5971070184679839885L;
	// List of broker object references, and number of connections for each broker
	private List<Pair<String, Integer>> brokers;
	private static Registry registry;
	private static int portNum;
	
	// pass in the portNumber for the registry
	public static void main(String[] args) {
		try {
			portNum = Integer.parseInt(args[0]);
			registry = LocateRegistry.createRegistry(portNum);
			Directory directory = new Directory();
			registry.rebind("Directory", directory);
		} catch (RemoteException e) {
			System.out.println("Error communicating with registry");
			e.printStackTrace();
		}
	}
	
	protected Directory() throws RemoteException {
		super();
		brokers = new ArrayList<Pair<String, Integer>>();
	}
	
	/**
	 * Returns a single broker for a new connection to connect to
	 * @return The object reference for a single broker
	 */
	@Override
	public String getBroker() {
		int min_i = 0;
		for (int i=1; i < brokers.size(); i++) {
			if (brokers.get(i).getValue1() < brokers.get(min_i).getValue1()) {
				min_i = i;
			}
		}
		brokers.set(min_i, brokers.get(min_i).setAt1(brokers.get(min_i).getValue1() + 1));
		System.out.println(brokers.get(min_i).getValue0() + " connected to, current number of connections: " 
				+ brokers.get(min_i).getValue1());
;		return brokers.get(min_i).getValue0();
	}

	/**
	 * Takes the IP and port associated with the new broker and returns the
	 * list of IP and ports for all other brokers
	 * @param IP address of new broker
	 * @param port number of new broker
	 * @return List of IP and port numbers for all other brokers
	 */
	@Override
	public synchronized List<String> addBroker(String brokerObjRef) {
		brokers.add(new Pair<String, Integer>(brokerObjRef, 0));
		System.out.println("Broker " + brokerObjRef + " added");
		return brokers.subList(0, brokers.size() - 1).stream().
				map(x -> x.getValue0()).collect(Collectors.toList());
	}
	
	
	@Override
	public void disconnectBroker(String IP, int port) {
		// TODO Auto-generated method stub

	}
}
