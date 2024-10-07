package response;

import java.util.List;

import org.javatuples.Triplet;

public class ShowSubsResponse extends Response {
	private static final long serialVersionUID = -4044061054179403210L;
	// [topic_id_1] [topic_name_1] [publisher_1]
	private List<Triplet<Integer, String, String>> subscriptions;

	public ShowSubsResponse(Status status, List<Triplet<Integer, String, String>> subscriptions) {
		super(status);
		this.subscriptions = subscriptions;
	}

	public List<Triplet<Integer, String, String>> getSubscriptions() {
		return subscriptions;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		subscriptions.forEach(x -> strBuilder.append(
				x.getValue0().toString() + " " + x.getValue1() + " " + x.getValue2() + "\n"));
		return strBuilder.toString();
	}
}
