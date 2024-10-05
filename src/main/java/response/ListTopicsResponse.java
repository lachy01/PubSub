package response;

import java.util.List;
import org.javatuples.Triplet;

public class ListTopicsResponse extends Response {
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
		return "ListTopicsResponse [topics=" + topics + "]";
	}
}
