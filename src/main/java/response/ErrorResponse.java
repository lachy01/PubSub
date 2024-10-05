package response;

public class ErrorResponse extends Response {
	private String errorMsg;

	public ErrorResponse(Status status, String errorMsg) {
		super(status);
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "Error: " + errorMsg;
	}
}
