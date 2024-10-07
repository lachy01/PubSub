package response;

import java.io.Serializable;

public class Response implements Serializable {
	private static final long serialVersionUID = -4044061054179403210L;
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
