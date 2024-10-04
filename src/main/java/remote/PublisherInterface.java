package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

import response.Response;

public interface PublisherInterface extends Remote {
	public Response createTopic(int ID, String name, String publisherName) throws RemoteException;
	public Response publish(int ID, String message, String publisherName) throws RemoteException;
	public Response showSubCounts(String publisherName) throws RemoteException;
	public Response deleteTopic(int ID, String publisherName) throws RemoteException;
}
