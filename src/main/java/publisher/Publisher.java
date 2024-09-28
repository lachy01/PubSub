package publisher;

import java.io.IOException;
import java.util.List;

import org.javatuples.Pair;

public class Publisher {
	private static final int PORT_MAX = 0xFFFF;
	private static String name;
	private List<Integer> topicIDs;
	
	public static void main(String[] args) {
		String directoryIP;
		int directoryPort;
		
		try {
			Pair<String, Integer> directoryInfo = readArgs(args);
			directoryIP = directoryInfo.getValue0();
			directoryPort = directoryInfo.getValue1();
		}
		catch (IOException e) {
		}
		
		
	}
	
	private static Pair<String, Integer> readArgs(String[] args) throws IOException {
		if (args.length != 3) {
			throw new IOException("Please enter username, broker-directory IP and port number\n "
							+ "Usage: java –jar publisher.jar <username> <directory_address> <port>");
		}
		
		name = args[0];
		String directoryIP = args[1];
		Integer port = 0;
		
		try {
			port = Integer.valueOf(args[2]);
			if (port < 0 || port > PORT_MAX)
		        throw new IllegalArgumentException("Port out of range:" + port);
			}
		catch (NumberFormatException e) {
			System.out.println("Port number must be an integer");
		}
		
		return new Pair<String, Integer>(directoryIP, port);
	}
	
	private void createTopic(int ID, String name) {
		
	}
	
	private void publish(int ID, String message) {
		
	}
	
	private void showSubCounts() {
		
	}
	
	private void deleteTopic(int ID) {
		
	}
}
