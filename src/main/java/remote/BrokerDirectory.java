package remote;

import java.util.List;

import org.javatuples.Pair;

public interface BrokerDirectory {
	public List<Pair<String, Integer>> addBroker(String IP, int port);
}
