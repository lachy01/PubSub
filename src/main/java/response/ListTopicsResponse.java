package response;

import java.util.List;
import org.javatuples.Triplet;

public class ListTopicsResponse extends Response {
	private static final long serialVersionUID = -4044061054179403210L;
	// [topic_id_1] [topic_name_1] [publisher_1]
	private List<Triplet<Integer, String, String>> topics;
	
	public ListTopicsResponse(Status status, List<Triplet<Integer, String, String>> topics) {
		super(status);
		this.topics = topics;
	}

	public List<Triplet<Integer, String, String>> getTopics() {
		return topics;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		topics.forEach(x -> strBuilder.append(
				x.getValue0().toString() + " " + x.getValue1() + " " + x.getValue2() + "\n"));
		return strBuilder.toString();
	}
}
