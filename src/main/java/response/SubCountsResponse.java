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

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		subscriberCounts.forEach(x -> strBuilder.append(
				x.getValue0().toString() + " " + x.getValue1() + " " + x.getValue2().toString() + "\n"));
		return strBuilder.toString();
	}
	
	
}
