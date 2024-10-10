package broker;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import response.ErrorResponse;
import response.Response;
import response.Status;

public class Topic {
	public final int ID;
	public final String name;
	public final String publisherName;
	private Set<String> subscriberNames = new HashSet<String>();
	
	public Topic(int iD, String name, String publisherName) {
		super();
		ID = iD;
		this.name = name;
		this.publisherName = publisherName;
	}
	
	/**
	 * Adds a subscriber to the list of subscribers on this topic
	 * @param subscriberName username of subscriber
	 * @return status of operation
	 * @throws RemoteException
	 */
	public synchronized Response subscribe(String subscriberName) throws RemoteException {
		if (subscriberNames.contains(subscriberName)) {
			return new ErrorResponse(Status.ALREADYSUB, "Subscriber has already subscribed to this topic.");
		}
		subscriberNames.add(subscriberName);
		return new Response(Status.SUCCESS);
	}
	
	/**
	 * Removes a subscriber from the list of subscribers on this topic
	 * @param subscriberName username of subscriber
	 * @return status of operation
	 * @throws RemoteException
	 */
	public synchronized Response unsubscribe(String subscriberName) throws RemoteException {
		if (!subscriberNames.contains(subscriberName)) {
			return new ErrorResponse(Status.SUBNOTFOUND, "Subscriber is not already subscribed to this topic.");
		}
		subscriberNames.remove(subscriberName);
		return new Response(Status.SUCCESS);
	}
	
	public List<String> getSubscriberNames() {
		return new ArrayList<String>(subscriberNames);
	}
	
	public int getSubCount() {
		return subscriberNames.size();
	}
}
