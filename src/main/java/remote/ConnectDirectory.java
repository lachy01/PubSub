package remote;

import org.javatuples.Pair;

public interface ConnectDirectory {
	public Pair<String, Integer> getBroker();
	public void disconnectBroker(String IP, int port);
}
