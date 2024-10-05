package response;

public class Response {
	private Status status;
	
	public Response(Status status) {
		super();
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return status.toString();
	}
}
