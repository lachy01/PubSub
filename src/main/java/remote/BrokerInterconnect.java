package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import response.Response;

public interface BrokerInterconnect extends Remote {
	/**
	 * Adds the incoming broker into the set of otherBrokers
	 * @param brokerObjRef RMI object reference for the incoming broker
	 * @return A response designating its success status
	 * @throws RemoteException
	 */
	public Response acceptConnection(String brokerObjRef) throws RemoteException;
	
	/**
	 * Returns the list of topics associated with this broker
	 * @return list of (topic_id, topic_name, publisher_name)
	 * @throws RemoteException
	 */
	public List<Triplet<Integer, String, String>> collectTopics() throws RemoteException;
	
	/**
	 * Returns whether the broker has the topic with this ID
	 * @param ID topicID in question
	 * @return true if broker has the topic
	 * @throws RemoteException
	 */
	public Boolean hasTopic(int ID) throws RemoteException;
	
	/**
	 * Returns whether the broker has the given subscriber connected to it
	 * @param subscriberName The subscribers username
	 * @return true if broker has the subscriber
	 * @throws RemoteException
	 */
	public Boolean hasSub(String subscriberName) throws RemoteException;
	
	/**
	 * Performs the subscription of this subscriber to this topic which is located
	 * on the current broker.
	 * @param ID of the topic to be subscribed to
	 * @param subscriberName the subscriber's username
	 * @return status of the operation
	 * @throws RemoteException
	 */
	public Response handleSubscription(int ID, String subscriberName) throws RemoteException;
	
	/**
	 * Performs the unsubscription of this subscriber from this topic which is located
	 * on the current broker.
	 * @param ID of the topic to be subscribed to
	 * @param subscriberName the subscriber's username
	 * @return status of the operation
	 * @throws RemoteException
	 */
	public Response handleUnsubscription(int ID, String subscriberName) throws RemoteException;
	
	/**
	 * Gets the topic name and publisher name for the given topic ID
	 * @param ID topic ID
	 * @return (topic_name, publisher_name) for this topic
	 * @throws RemoteException
	 */
	public Pair<String, String> getTopicInfo(int ID) throws RemoteException; 

	/**
	 * Sends this message to the given subscriber which is connected to this broker
	 * @param subscriberName the username of the subscriber
	 * @param message the message to be sent
	 * @return status of the operation
	 * @throws RemoteException
	 */
	public Response sendToSub(String subscriberName, String message) throws RemoteException;
	
	/**
	 * Removes this topic from the list of subscribed topics for this subscriber
	 * who is connected to this broker.
	 * @param ID topic ID
	 * @param subscriberName username of subscriber
	 * @throws RemoteException
	 */
	public void removeSubscriber(int ID, String subscriberName) throws RemoteException;
	
	/**
	 * Removes the topic from the topic routing map on this broker.
	 * @param ID topic ID
	 * @throws RemoteException
	 */
	public void clearTopic(int ID) throws RemoteException;
}
