package response;

public class ErrorResponse extends Response {
	private String errorMsg;

	public ErrorResponse(Status status, String errorMsg) {
		super(status);
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}
