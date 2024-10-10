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
	
	/**
	 * Runs the Subscriber, connecting it to the RMI Registry, using it to access the 
	 * Directory and get a Broker to connect to. Opens a server socket to receive messages from 
	 * the broker and registers itself with the broker. Starts a heartbeat with the broker
	 * and awaits input from the console.
	 * @param args java –jar subscriber.jar <username> <registry_address> <registry_port> <my_address> <my_port>
	 */
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
			try {
				//Ensure the listenForMsgs thread has time to open the socket before
				//registering to avoid the broker trying to connect to a non-existant
				//socket.
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			// Allow the other thread to begin accepting port connection
			broker.registerSubscriber(username, myIP, myPort);
			
			Thread heartbeatThread = new Thread(() -> performHeartbeats());
			heartbeatThread.start();
			
			
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
	
	/**
	 * Processes the input line from the console and calls the respective RMI methods on the
	 * connected broker to serve the request, finally printing the response.
	 * @param input a line of input from the console
	 */
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
	
	/**
	 * Pings the connected broker with a heartbeat every 500 milliseconds
	 */
	private static void performHeartbeats() {
		while (true) {
			try {
				broker.heartbeat(username);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Listens for incoming messages from the broker socket and prints them as they come.
	 */
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
	
	/**
	 * Parses the command-line arguments
	 * @param args command-line arguments
	 * @return (registryIP, registryPort) for the RMI registry
	 * @throws IOException
	 */
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
