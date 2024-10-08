package publisher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.javatuples.Pair;

import remote.ConnectDirectory;
import remote.PublisherInterface;
import response.ErrorResponse;
import response.Response;
import response.Status;

public class Publisher {
	private static final int PORT_MAX = 0xFFFF;
	private static String username;
	//private List<Integer> topicIDs;
	private static Registry registry;
	private static PublisherInterface broker;
	private static String myIP;
	private static int myPort;
	
	//Usage: java –jar publisher.jar <username> <registry_address> <registry_port> <my_address> <my_port>
	public static void main(String[] args) {
		String registryIP;
		int registryPort;
		
		try {
			Pair<String, Integer> registryInfo = readArgs(args);
			registryIP = registryInfo.getValue0();
			registryPort = registryInfo.getValue1();
			registry = LocateRegistry.getRegistry(registryIP, registryPort);
			
			ConnectDirectory directory = (ConnectDirectory) registry.lookup("Directory");
			String brokerRef = directory.getBroker();
			broker = (PublisherInterface) registry.lookup(brokerRef);
			broker.registerPublisher(username);
			System.out.println("Connected to broker " + brokerRef);
			
			BufferedReader reader = new BufferedReader(
		            new InputStreamReader(System.in));
			while (true) {
				readInstructions(reader.readLine());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (NotBoundException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void readInstructions(String input) {
		String[] strings = input.split(" ");
		int topicID;
		
		if (strings.length < 1) {
			System.out.println("Please select command: create, publish, show, delete.");
			return;
		}
		
		Response response = new ErrorResponse(Status.ERROR, "Please select command: create, publish, show, delete.");
		
		switch (strings[0]) {
		case "create":
			if (strings.length != 3) {
				System.out.println("Usage: create {topic_id} {topic_name} #create a new topic");
				return;
			}
			
			try {topicID = Integer.parseInt(strings[1]);}
			catch (NumberFormatException e) {
				System.out.println("Please enter a valid topic_id");
				return;
			}
			String topicName = strings[2];
			
			try {
				response = broker.createTopic(topicID, topicName, username);
			} catch (RemoteException e) {
				System.out.println("Error communicating with server");
			}
			break;
			
		case "publish":
			if (strings.length < 3) {
				System.out.println("Usage: publish {topic_id} {message} # publish a message to an existing topic");
				return;
			}
			
			try {topicID = Integer.parseInt(strings[1]);}
			catch (NumberFormatException e) {
				System.out.println("Please enter a valid topic_id");
				return;
			}
			
			StringBuilder strBuilder = new StringBuilder();
			for (int i=2; i<strings.length; i++) {
				strBuilder.append(strings[i]);
			}
			String message = strBuilder.toString();
			
			try {
				response = broker.publish(topicID, message, username);
			} catch (RemoteException e) {
				System.out.println("Error communicating with server");
			}
			break;
			
		case "show":
			if (strings.length != 2) {
				System.out.println("Usage: show {topic_id} #show subsriber count for current publisher");
				return;
			}
			
			try {
				response = broker.showSubCounts(username);
			} catch (RemoteException e) {
				System.out.println("Error communicating with server");
			}
			break;
			
		case "delete":
			if (strings.length != 2) {
				System.out.println("Usage: delete {topic_id} #delete a topic");
				return;
			}
			
			try {topicID = Integer.parseInt(strings[1]);}
			catch (NumberFormatException e) {
				System.out.println("Please enter a valid topic_id");
				return;
			}
			
			try {
				response = broker.deleteTopic(topicID, username);
			} catch (RemoteException e) {
				System.out.println("Error communicating with server");
			}
			break;
			
		}
		
		System.out.println(response.toString());
	}
	
	
	private static Pair<String, Integer> readArgs(String[] args) throws IOException {
		if (args.length != 5) {
			throw new IOException("Please enter username, registry IP and port number\n "
							+ "Usage: java –jar publisher.jar <username> <registry_address> <registry_port> <my_address> <my_port>");
		}
		
		username = args[0];
		String registryIP = args[1];
		Integer registryPort = 0;
		
		try {
			registryPort = Integer.valueOf(args[2]);
			if (registryPort < 0 || registryPort > PORT_MAX)
		        throw new IllegalArgumentException("Port out of range:" + registryPort);
			}
		catch (NumberFormatException e) {
			System.out.println("Port number must be an integer");
		}
		
		myIP = args[3];
		try {
			myPort = Integer.valueOf(args[4]);
			if (myPort < 0 || myPort > PORT_MAX)
		        throw new IllegalArgumentException("Port out of range:" + myPort);
			}
		catch (NumberFormatException e) {
			System.out.println("Port number must be an integer");
		}
		
		return new Pair<String, Integer>(registryIP, registryPort);
	}
}
