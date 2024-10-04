package broker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import remote.*;
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
	
	public synchronized Response subscribe(String subscriberName) {
		if (subscriberNames.contains(subscriberName)) {
			return new Response(Status.ALREADYSUB);
		}
		subscriberNames.add(subscriberName);
		return new Response(Status.SUCCESS);
	}
	
	public synchronized Response unsubscribe(String subscriberName) {
		if (!subscriberNames.contains(subscriberName)) {
			return new Response(Status.SUBNOTFOUND);
		}
		subscriberNames.remove(subscriberName);
		return new Response(Status.SUCCESS);
	}
	
	public List<String> getSubscriberNames() {
		return Arrays.asList((String[])subscriberNames.toArray());
	}
}
