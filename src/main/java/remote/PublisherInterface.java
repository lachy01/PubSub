package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import response.Response;

public interface PublisherInterface extends Remote {
	
	/**
	 * Creates an entry in the publisherTopics for this incoming publisher
	 * @param publisherName username of the new publisher
	 * @throws RemoteException
	 */
	public void registerPublisher(String publisherName) throws RemoteException;
	
	/**
	 * Creates a new topic on this broker
	 * @param ID topic ID
	 * @param name topic name
	 * @param publisherName username of the publisher
	 * @return status of the operation
	 * @throws RemoteException
	 */
	public Response createTopic(int ID, String name, String publisherName) throws RemoteException;
	
	/**
	 * Publishes a message to this topic, prepending the message with datetime and topic details.
	 * It will send the message to all subscribers of the topic, locating them and updating their
	 * route mapping if they are not local.
	 * @param ID topic ID
	 * @param message the message to be sent (without datetime or topic details)
	 * @param publisherName username of the publisher
	 * @return status of the operation
	 * @throws RemoteException
	 */
	public Response publish(int ID, String message, String publisherName) throws RemoteException;
	
	/**
	 * Returns a response containing the subscriber counts for each topic which the subscriber has
	 * created. 
	 * @param publisherName username of the publisher
	 * @return a ShowSubsResponse object if successful, and an ErrorResponse otherwise.
	 * @throws RemoteException
	 */
	public Response showSubCounts(String publisherName) throws RemoteException;
	
	/**
	 * Deletes the given topic from this broker. It will notify all of the subscribers of the 
	 * deletion and unsubscribe them from it.
	 * @param ID topic ID
	 * @param publisherName username of the publisher
	 * @return status of the operation
	 * @throws RemoteException
	 */
	public Response deleteTopic(int ID, String publisherName) throws RemoteException;
	
	/**
	 * Registers a heartbeat with the broker, giving a current timestamp associated with this 
	 * username.
	 * @param username
	 * @throws RemoteException
	 */
	public void heartbeat(String username) throws RemoteException;
}
