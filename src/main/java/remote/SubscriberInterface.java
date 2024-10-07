package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import response.Response;

public interface SubscriberInterface extends Remote {
	public void registerSubscriber(String subscriberName, String IP, int port) throws RemoteException;
	public Response listAllTopics() throws RemoteException;
	public Response subscribe(int ID, String subscriberName) throws RemoteException;
	public Response unsubscribe(int ID, String subscriberName) throws RemoteException;
	public Response showSubs(String subscriberName) throws RemoteException;
}
