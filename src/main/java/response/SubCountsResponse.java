package response;

import java.util.List;

import org.javatuples.Triplet;

public class SubCountsResponse extends Response {
	private List<Triplet<Integer, String, Integer>> subscriberCounts;
	
	public SubCountsResponse(Status status, List<Triplet<Integer, String, Integer>> subscriberCounts) {
		super(status);
		this.subscriberCounts = subscriberCounts;
	}

	public List<Triplet<Integer, String, Integer>> getSubscriberCounts() {
		return subscriberCounts;
	}
}
