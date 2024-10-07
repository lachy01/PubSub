package broker;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import remote.BrokerDirectory;
import remote.BrokerInterconnect;
import remote.PublisherInterface;
import remote.SubscriberInterface;
import response.ErrorResponse;
import response.ListTopicsResponse;
import response.Response;
import response.ShowSubsResponse;
import response.Status;
import response.SubCountsResponse;

public class Broker extends UnicastRemoteObject implements PublisherInterface, SubscriberInterface, BrokerInterconnect{
	private static final long serialVersionUID = 1960092328885316174L;
	private static Registry registry;
	private static BrokerDirectory directory;
	private static String objectRef;
	private static List<BrokerInterconnect> otherBrokers;
	private HashMap<Integer, Topic> topics; 					// Maps topicId -> topic
	private HashMap<String, List<Integer>> publisherTopics; 	// Maps publisherName -> [topic_id]
	private HashMap<String, Socket> subscriberSockets; 			// Maps subscriberName -> socket
	private HashMap<Integer, BrokerInterconnect> topicRouteMap; // Maps topicID -> broker
	private HashMap<String, BrokerInterconnect> subscriberRouteMap; // Maps subscriberName -> broker
	// Maps subscriberName -> [(topic_id, topic_name, publisher_name)]
	private HashMap<String, List<Triplet<Integer, String, String>>> subscriberSubs; 
	
	// For the broker, you pass in the port number for the broker, 
	// the IP and port number of the RMI Registry for the Directory
	public static void main(String[] args) {
		int portNum = Integer.parseInt(args[0]);
		objectRef = "Broker" + portNum;
		
		String registryIP = args[1];
		int registryPort = Integer.parseInt(args[2]);
		
		try {
			registry = LocateRegistry.getRegistry(registryIP, registryPort);
			directory = (BrokerDirectory) registry.lookup("Directory");
		}
		catch (RemoteException e) {
			System.out.println("Error communicating with registry.");
			e.printStackTrace();
		}
		catch (NotBoundException e) {
			System.out.println("Error: Directory not bound to registry.");
			e.printStackTrace();
		}
		
		connectToBrokers();
	}
	
	public Broker() throws RemoteException {
		super();
		topics = new HashMap<Integer, Topic>();
		publisherTopics = new HashMap<String, List<Integer>>();
		otherBrokers = new ArrayList<BrokerInterconnect>();	// Maps publisherName -> [topic_id]
		subscriberSockets = new HashMap<String, Socket>(); 			// Maps subscriberName -> socket
		topicRouteMap = new HashMap<Integer, BrokerInterconnect>(); // Maps topicID -> broker
		subscriberRouteMap = new HashMap<String, BrokerInterconnect>(); // Maps subscriberName -> broker
		// Maps subscriberName -> [(topic_id, topic_name, publisher_name)]
		subscriberSubs = new HashMap<String, List<Triplet<Integer, String, String>>>(); 
	}
	
	
	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	// INTERBROKER METHODS
	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	

	@Override
	public List<Triplet<Integer, String, String>> collectTopics() throws RemoteException {
		List<Triplet<Integer, String, String>> topicsList = new ArrayList<Triplet<Integer, String, String>>();
		for (Topic topic: topics.values()) {
			topicsList.add(new Triplet<Integer, String, String>(topic.ID, topic.name, topic.publisherName));
		}
		return topicsList;
	}
	
	@Override
	public Boolean hasTopic(int ID) throws RemoteException {
		if (topics.containsKey(ID)) {
			return true;
		}
		return false;
	}
	
	@Override
	public Boolean hasSub(String subscriberName) throws RemoteException {
		if (subscriberSockets.containsKey(subscriberName)) {
			return true;
		}
		return false;
	}
	
	// Returns (topic_name, publisher_name)
	@Override
	public Pair<String, String> getTopicInfo(int ID) throws RemoteException {
		return new Pair<String, String>(topics.get(ID).name, topics.get(ID).publisherName);
	}
	
	@Override
	public synchronized Response handleSubscription(int ID, String subscriberName) throws RemoteException {
		if (!topics.containsKey(ID)) {
			return new ErrorResponse(Status.TOPICNOTFOUND, "Topic could not be found.");
		}
		return topics.get(ID).subscribe(subscriberName);
	}
	
	@Override
	public synchronized Response handleUnsubscription(int ID, String subscriberName) throws RemoteException {
		if (!topics.containsKey(ID)) {
			return new ErrorResponse(Status.TOPICNOTFOUND, "Topic could not be found.");
		}
		return topics.get(ID).unsubscribe(subscriberName);
	}
	
	@Override
	public void clearTopic(int ID) throws RemoteException {
		topicRouteMap.remove(ID);
	}

	@Override
	public Response sendToSub(String subscriberName, String message) throws RemoteException {
		Socket socket = subscriberSockets.get(subscriberName);
		if (socket == null) {
			return new ErrorResponse(Status.SUBNOTFOUND, "Subscriber could not be located.");
		}
		
		if (socket.isClosed()) {
			return new ErrorResponse(Status.SUBNOTFOUND, "Subscriber connection lost.");
		}
		
		try {
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			output.writeUTF(message);
			output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new Response(Status.SUCCESS);
	}
	
	public static void connectToBrokers() {
		try {
			// First, setup this broker and register it
			Broker broker = new Broker();
			registry.bind(objectRef, broker);
			
			// Then, connect it to all the other brokers
			List<String> otherBrokerRefs = directory.addBroker(objectRef);
			BrokerInterconnect otherBroker;
			Response response;
			for (String otherBrokerRef: otherBrokerRefs) {
				otherBroker = (BrokerInterconnect) registry.lookup(otherBrokerRef);
				otherBrokers.add(otherBroker);
				response = otherBroker.acceptConnection(objectRef);
				if (response instanceof ErrorResponse) {
					throw new Exception(response.toString());
				}
				
			}
			System.out.println("Successfully connected.");
		}
		catch (RemoteException e) {
			System.out.println("Error communicating with registry.");
			e.printStackTrace();
		}
		catch (NotBoundException e) {
			System.out.println("Error: a broker in the network has not been bound.");
			e.printStackTrace();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public Response acceptConnection(String otherBrokerRef) throws RemoteException {
		try {
			BrokerInterconnect otherBroker = (BrokerInterconnect) registry.lookup(otherBrokerRef);
			otherBrokers.add(otherBroker);
		} 
		catch (RemoteException e) {
			System.out.println("Error communicating with registry.");
			return new ErrorResponse(Status.ERROR, "Error communicating with registry.");
		} catch (NotBoundException e) {
			System.out.println("Error: a broker in the network has not been bound.");
			return new ErrorResponse(Status.ERROR, "Error: this broker has not been bound.");
		}
		return new Response(Status.SUCCESS);
	}
	

	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	// SUBSCRIBER METHODS
	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	
	@Override
	public void registerSubscriber(String subscriberName, String IP, int port) throws RemoteException {
		try
		{
			Socket socket = new Socket(IP, port);
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
		    String sendData ="Connected to broker";
	    	output.writeUTF(sendData);
	    	output.flush();
	    	
	    	subscriberSockets.put(subscriberName, socket);
	    	subscriberRouteMap.put(subscriberName, this);
	    	subscriberSubs.put(subscriberName, new ArrayList<Triplet<Integer, String, String>>());
	    	System.out.println("Subscriber connected.");
		} 
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public Response listAllTopics() throws RemoteException {
		List<Triplet<Integer, String, String>> topicsList = collectTopics();
		Stream<Triplet<Integer, String, String>> topicStream = topicsList.stream();
		for (BrokerInterconnect otherBroker: otherBrokers) {
			topicStream = Stream.concat(topicStream, otherBroker.collectTopics().stream());
		}
		return new ListTopicsResponse(Status.SUCCESS, topicStream.collect(Collectors.toList())); 
	}

	@Override
	public Response subscribe(int ID, String subscriberName) throws RemoteException {
		if (!topicRouteMap.containsKey(ID)) {
			for (BrokerInterconnect otherBroker : otherBrokers) {
				if (otherBroker.hasTopic(ID)) {
					topicRouteMap.put(ID, otherBroker);
					break;
				}
			}
			return new ErrorResponse(Status.TOPICNOTFOUND, "This topic cannot be found.");
		}
		BrokerInterconnect topicsBroker = topicRouteMap.get(ID);
		Pair<String, String> topicInfo = topicsBroker.getTopicInfo(ID);
		subscriberSubs.get(subscriberName).add(new Triplet<Integer, String, String>(ID, 
				topicInfo.getValue0(), topicInfo.getValue1()));
		return topicsBroker.handleSubscription(ID, subscriberName);
	}

	@Override
	public Response unsubscribe(int ID, String subscriberName) throws RemoteException {
		if (!topicRouteMap.containsKey(ID)) {
			for (BrokerInterconnect otherBroker : otherBrokers) {
				if (otherBroker.hasTopic(ID)) {
					topicRouteMap.put(ID, otherBroker);
					break;
				}
			}
			return new ErrorResponse(Status.TOPICNOTFOUND, "This topic cannot be found.");
		}
		BrokerInterconnect topicsBroker = topicRouteMap.get(ID);
		removeSubscriber(ID, subscriberName);
		return topicsBroker.handleUnsubscription(ID, subscriberName);
	}
	
	@Override
	public void removeSubscriber(int ID, String subscriberName) throws RemoteException {
		subscriberSubs.get(subscriberName).removeIf(x -> x.getValue0() == ID);
	}

	@Override
	public Response showSubs(String subscriberName) throws RemoteException {
		return new ShowSubsResponse(Status.SUCCESS, subscriberSubs.get(subscriberName));
	}

	
	
	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	// PUBLISHER METHODS
	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	
	@Override
	public void registerPublisher(String publisherName) throws RemoteException {
		publisherTopics.put(publisherName, new ArrayList<Integer>());
	}
	
	@Override
	public Response createTopic(int ID, String name, String publisherName) throws RemoteException {
		if (topics.containsKey(ID)) {
			return new ErrorResponse(Status.ERROR, "topic_id already taken.");
		}
		Topic topic = new Topic(ID, name, publisherName);
		topics.put(ID, topic);
		publisherTopics.get(publisherName).add(ID);
		topicRouteMap.put(ID, this);
		return new Response(Status.SUCCESS);
	}

	@Override
	public Response publish(int ID, String message, String publisherName) throws RemoteException {
		Topic topic = topics.get(ID);
		if (topic == null) {
			return new ErrorResponse(Status.ERROR, "Topic not found.");
		}
		if (!topic.publisherName.equals(publisherName)) {
			return new ErrorResponse(Status.ACCESSDENIED, "Topic is not managed by this publisher, it is managed by " 
					+ topic.publisherName);
		}
		
		Response response = new Response(Status.SUCCESS);
		for (String subscriberName: topic.getSubscriberNames()) {
			if (!subscriberRouteMap.containsKey(subscriberName) && !findSubscriber(subscriberName)) {
				return new ErrorResponse(Status.SUBNOTFOUND, "Subscriber " + subscriberName + " could not be located.");
			}
			response = subscriberRouteMap.get(subscriberName).sendToSub(subscriberName, message);
			if (response.getStatus() != Status.SUCCESS) {return response;}
		}
		return response;
	}

	/**
	 * Updates the subscriberRouteMap with the location of the subscriber, returns false if not found
	 * @param subscriberName
	 * @return whether or not sub was found.
	 */
	private Boolean findSubscriber(String subscriberName) {
		for (BrokerInterconnect otherBroker : otherBrokers) {
			try {
				if (otherBroker.hasSub(subscriberName)) {
					subscriberRouteMap.put(subscriberName, otherBroker);
					return true;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	@Override
	public Response showSubCounts(String publisherName) throws RemoteException {
		List<Triplet<Integer, String, Integer>> subCounts = new ArrayList<Triplet<Integer, String, Integer>>();
		for (int topicID: publisherTopics.get(publisherName)) {
			Topic topic = topics.get(topicID);
			subCounts.add(new Triplet<Integer, String, Integer>(topicID, topic.name, topic.getSubCount()));
		}
		return new SubCountsResponse(Status.SUCCESS, subCounts);
	}

	@Override
	public Response deleteTopic(int ID, String publisherName) throws RemoteException {
		if (!publisherTopics.get(publisherName).contains(ID)) {
			return new ErrorResponse(Status.ACCESSDENIED, "Publisher did not create this topic.");
		}
		String errorMsg = null;
		
		List<String> subscribers = topics.get(ID).getSubscriberNames();
		Response response = publish(ID, "Topic " + ID + " has been deleted", publisherName);
		if (response instanceof ErrorResponse) {
			errorMsg = response.toString();
		}
		// Subscriber should now be in the route map as publish will update it
		for (String subscriberName: subscribers) {
			BrokerInterconnect otherBroker = subscriberRouteMap.get(subscriberName);
			assert(otherBroker != null);
			otherBroker.removeSubscriber(ID, subscriberName);
		}
		for (BrokerInterconnect broker: otherBrokers) {
			broker.clearTopic(ID);
		}
		
		publisherTopics.get(publisherName).remove(Integer.valueOf(ID));
		topics.remove(ID);
		
		if (errorMsg != null) {
			return new ErrorResponse(Status.ERROR, errorMsg);
		}
		
		return new Response(Status.SUCCESS);
	}


	
}
