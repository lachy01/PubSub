package broker;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.javatuples.Triplet;

import remote.PublisherInterface;
import remote.SubscriberInterface;
import response.ErrorResponse;
import response.Response;
import response.Status;
import response.SubCountsResponse;

public class Broker implements PublisherInterface, SubscriberInterface{
	private static Registry registry;
	private HashMap<Integer, Topic> topics;
	private HashMap<String, List<Integer>> publisherTopics;
	
	// For the broker, you pass in the port number for the broker, 
	// the IP and port number of the RMI Registry for the Directory
	public static void main(String[] args) {
		int portNum = Integer.parseInt(args[0]);
		String registryIP = args[1];
		int registryPort = Integer.parseInt(args[2]);
		
		try {
			registry = LocateRegistry.getRegistry(registryIP, registryPort);
		}
		catch (Exception e) {
			
		}
	}
	
	public Broker() {
		topics = new HashMap<Integer, Topic>();
		publisherTopics = new HashMap<String, List<Integer>>();
	}
	
	

	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	// SUBSCRIBER METHODS
	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	
	@Override
	public Response listAllTopics() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response subscribe(int ID, String subscriberName) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response unsubscribe(int ID, String subscriberName) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response showSubs(String subscriberName) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	// PUBLISHER METHODS
	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	
	public void registerPublisher(String publisherName) {
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
		return new Response(Status.SUCCESS);
	}

	@Override
	public Response publish(int ID, String message, String publisherName) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
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
		
		List<String> subscribers = topics.get(ID).getSubscriberNames();
		// TODO Notify subscribers of deletion
		
		return new Response(Status.SUCCESS);
	}
	
	
}
