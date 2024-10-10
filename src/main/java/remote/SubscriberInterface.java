package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import response.Response;

public interface SubscriberInterface extends Remote {
	/**
	 * Intialises all the relevant data structures for the subscriber and creates a socket on the broker
	 * which connects to an awaiting socket on the subscribers side.
	 * @param subscriberName username of the subscriber
	 * @param IP address of the subscriber's socket
	 * @param port port number of the subscriber's socket
	 * @throws RemoteException
	 */
	public void registerSubscriber(String subscriberName, String IP, int port) throws RemoteException;
	
	/**
	 * Returns a list of all available topics across the network
	 * @return a ListTopicsResponse object if successful and an ErrorResponse otherwise
	 * @throws RemoteException
	 */
	public Response listAllTopics() throws RemoteException;
	
	/**
	 * Subscribes the given subscriber to the given topic. Updates the topic route map to locate the
	 * topic on the correct broker.
	 * @param ID topic ID
	 * @param subscriberName username of the subscriber
	 * @return status of the operation
	 * @throws RemoteException
	 */
	public Response subscribe(int ID, String subscriberName) throws RemoteException;
	
	/**
	 * Unsubscribers the given subscriber from the given topic. Locates the broker which manages the 
	 * topic and unsubscribes through it.
	 * @param ID topic ID
	 * @param subscriberName username of the subscriber
	 * @return status of the operation
	 * @throws RemoteException
	 */
	public Response unsubscribe(int ID, String subscriberName) throws RemoteException;
	
	/**
	 * Returns the current subscriptions for the given subscriber.
	 * @param subscriberName username of the subscriber
	 * @return a ShowSubsResponse if successful, and an ErrorResponse otherwise
	 * @throws RemoteException
	 */
	public Response showSubs(String subscriberName) throws RemoteException;
	
	/**
	 * Registers a heartbeat with the broker, giving a current timestamp associated with this 
	 * username.
	 * @param username
	 * @throws RemoteException
	 */
	public void heartbeat(String username) throws RemoteException;
}
