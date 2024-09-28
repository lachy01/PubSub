package remote;

public class Response {
	private Status status;
	private Message message = null;
	private String errorMsg = null;
	
	public Response(Status status, Message message, String errorMsg) {
		super();
		this.status = status;
		this.message = message;
		this.errorMsg = errorMsg;
	}
	
	public Response(Status status) {
		super();
		this.status = status;
	}
}
