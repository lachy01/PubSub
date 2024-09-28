package remote;

import java.sql.Timestamp;

public class Message {
	private Timestamp datetime;
	private int topicID;
	private String topicName;
	private String message;
	
	public Message(Timestamp datetime, int topicID, String topicName, String message) {
		super();
		this.datetime = datetime;
		this.topicID = topicID;
		this.topicName = topicName;
		this.message = message;
	}
	
	public Timestamp getDatetime() {
		return datetime;
	}
	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}
	public int getTopicID() {
		return topicID;
	}
	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
