package broker;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import remote.PublisherInterface;
import remote.SubscriberInterface;
import response.Response;

public class Broker implements PublisherInterface, SubscriberInterface{
	private static Registry registry;
	
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

	@Override
	public Response createTopic(int ID, String name, String publisherName) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response publish(int ID, String message, String publisherName) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response showSubCounts(String publisherName) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response deleteTopic(int ID, String publisherName) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
