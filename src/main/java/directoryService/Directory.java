package directoryService;

import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import remote.BrokerDirectory;
import remote.ConnectDirectory;

public class Directory implements BrokerDirectory, ConnectDirectory {
	private List<Triplet<String, Integer, Integer>> brokers;
	
	@Override
	public Pair<String, Integer> getBroker() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disconnectBroker(String IP, int port) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Pair<String, Integer>> addBroker(String IP, int port) {
		// TODO Auto-generated method stub
		return null;
	}

}
