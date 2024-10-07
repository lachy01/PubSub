package response;

import java.io.Serializable;

public enum Status implements Serializable {
	SUCCESS,
	ALREADYSUB,
	SUBNOTFOUND,
	ACCESSDENIED,
	ERROR,
	TOPICNOTFOUND
}
