package response;

public class ErrorResponse extends Response {
	private static final long serialVersionUID = -4044061054179403210L;
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
