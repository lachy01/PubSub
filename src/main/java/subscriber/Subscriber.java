package subscriber;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

import remote.ConnectDirectory;
import remote.SubscriberInterface;
import response.ErrorResponse;
import response.Response;
import response.Status;

public class Subscriber {
	private static final int PORT_MAX = 0xFFFF;
	private static String name;
	private List<Integer> subscribedIDs;
	private static Registry registry;
	private static SubscriberInterface broker;
	
	public static void main(String[] args) {
		String registryIP;
		int registryPort;
		
		try {
			Pair<String, Integer> registryInfo = readArgs(args);
			registryIP = registryInfo.getValue0();
			registryPort = registryInfo.getValue1();
			registry = LocateRegistry.getRegistry(registryIP, registryPort);
			
			ConnectDirectory directory = (ConnectDirectory) registry.lookup("Directory");
			directory.getBroker();
			
			BufferedReader reader = new BufferedReader(
		            new InputStreamReader(System.in));
			while (true) {
				readInstructions(reader.readLine());
			}
		}
		catch (IOException e) {
			
		}
		catch (NotBoundException e) {
			
		}
		
	}
	
	private static void readInstructions(String input) {
		String[] strings = input.split(" ");
		
		if (strings.length < 1) {
			System.out.println("Please select command: list, sub, current, unsub.");
			return;
		}
		
		Response response = new ErrorResponse(Status.ERROR, "Please select command: list, sub, current, unsub.");
		
		switch (strings[0]) {
		case "list":
			try {response = broker.listAllTopics();}
			catch (RemoteException e) {
				response = new ErrorResponse(Status.ERROR, "Error communicating with server.");
			}
			break;
		
		case "sub":
			// TODO rest
		}
		
		System.out.println(response.toString());
	}
	
	public Subscriber(String name) {
		subscribedIDs = new ArrayList<Integer>();
	}
	
	private void listAllTopics() {
		
	}
	
	private void subscribe(int ID) {
		
	}
	
	private void showSubs() {
		
	}
	
	private void unsubscribe(int ID) {
		
	}
	
	private static Pair<String, Integer> readArgs(String[] args) throws IOException {
		if (args.length != 3) {
			throw new IOException("Please enter username, broker-directory IP and port number\n "
							+ "Usage: java –jar publisher.jar <username> <directory_address> <port>");
		}
		
		name = args[0];
		String registryIP = args[1];
		Integer port = 0;
		
		try {
			port = Integer.valueOf(args[2]);
			if (port < 0 || port > PORT_MAX)
		        throw new IllegalArgumentException("Port out of range:" + port);
			}
		catch (NumberFormatException e) {
			System.out.println("Port number must be an integer");
		}
		
		return new Pair<String, Integer>(registryIP, port);
	}
}
