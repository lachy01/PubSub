package subscriber;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.net.ServerSocketFactory;

import org.javatuples.Pair;

import remote.ConnectDirectory;
import remote.SubscriberInterface;
import response.ErrorResponse;
import response.Response;
import response.Status;

public class Subscriber {
	private static final int PORT_MAX = 0xFFFF;
	private static String username;
	private static Registry registry;
	private static SubscriberInterface broker;
	private static String myIP;
	private static int myPort;
	
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
			broker = (SubscriberInterface) registry.lookup(brokerRef);
			
			// Start a new thread for a listening connection
			Thread listenThread = new Thread(() -> listenForMsgs());
			listenThread.start();
			Thread.sleep(1000); // Allow the other thread to begin accepting port connection
			broker.registerSubscriber(username, myIP, myPort);
			
			
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
			if (strings.length != 2) {
				System.out.println("Usage: sub {topic_id} #subsribe to a topic");
				return;
			}
			
			try {topicID = Integer.parseInt(strings[1]);}
			catch (NumberFormatException e) {
				System.out.println("Please enter a valid topic_id");
				return;
			}
			
			try {response = broker.subscribe(topicID, username);}
			catch (RemoteException e) {
				response = new ErrorResponse(Status.ERROR, "Error communicating with server.");
			}
			break;
		
		case "current":
			try {response = broker.showSubs(username);}
			catch (RemoteException e) {
				response = new ErrorResponse(Status.ERROR, "Error communicating with server.");
			}
			break;
			
		case "unsub":
			if (strings.length != 2) {
				System.out.println("Usage: unsub {topic_id} #unsubsribe from a topic");
				return;
			}
			
			try {topicID = Integer.parseInt(strings[1]);}
			catch (NumberFormatException e) {
				System.out.println("Please enter a valid topic_id");
				return;
			}
			
			try {response = broker.unsubscribe(topicID, username);}
			catch (RemoteException e) {
				response = new ErrorResponse(Status.ERROR, "Error communicating with server.");
			}
			break;
		}
		
		System.out.println(response.toString());
	}
	
	public Subscriber(String name) {
		
	}
	
	private static void listenForMsgs() {
		ServerSocketFactory factory = ServerSocketFactory.getDefault();
		
		try(ServerSocket server = factory.createServerSocket(myPort))
		{
			// Wait for connections.
			Socket socket = server.accept();
			DataInputStream input = new DataInputStream(socket.getInputStream());
			
		    while(true)
		    {
		    	if(input.available()>0) {
		    		String message = input.readUTF();
		    		System.out.println(message);
		    	}
		    }
			
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	private static Pair<String, Integer> readArgs(String[] args) throws IOException {
		if (args.length != 5) {
			throw new IOException("Please enter username, registry IP and port number, and the subscriber's IP and port number\n "
							+ "Usage: java –jar publisher.jar <username> <registry_address> <registry_port> <my_address> <my_port>");
		}
		
		username = args[0];
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
		
		myIP = args[3];
		try {
			myPort = Integer.valueOf(args[4]);
			if (myPort < 0 || myPort > PORT_MAX)
		        throw new IllegalArgumentException("Port out of range:" + myPort);
			}
		catch (NumberFormatException e) {
			System.out.println("Port number must be an integer");
		}
		
		return new Pair<String, Integer>(registryIP, port);
	}
}
