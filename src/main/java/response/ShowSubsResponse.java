package response;

import java.util.List;

import org.javatuples.Triplet;

public class ShowSubsResponse extends Response {
	private List<Triplet<Integer, String, String>> subscriptions;

	public ShowSubsResponse(Status status, List<Triplet<Integer, String, String>> subscriptions) {
		super(status);
		this.subscriptions = subscriptions;
	}

	public List<Triplet<Integer, String, String>> getSubscriptions() {
		return subscriptions;
	}
	
	
}
