package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import response.Response;

public interface BrokerInterconnect extends Remote {
	public Response acceptConnection(String brokerObjRef) throws RemoteException;
	// list of (topic_id, topic_name, publisher_name)
	public List<Triplet<Integer, String, String>> collectTopics() throws RemoteException;
	public Boolean hasTopic(int ID) throws RemoteException;
	public Boolean hasSub(String subscriberName) throws RemoteException;
	public Response handleSubscription(int ID, String subscriberName) throws RemoteException;
	public Response handleUnsubscription(int ID, String subscriberName) throws RemoteException;
	public Pair<String, String> getTopicInfo(int ID) throws RemoteException; // Returns (topic_name, publisher_name)
	public Response sendToSub(String subscriberName, String message) throws RemoteException;
	public void removeSubscriber(int ID, String subscriberName) throws RemoteException;
	public void clearTopic(int ID) throws RemoteException;
}
