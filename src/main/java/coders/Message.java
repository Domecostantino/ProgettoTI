package coders;

/*
 * TEMPORANEO - bisogna decidere come formalizzare un header che vada bene per tutte le codifiche di sorgente
 */

public class Message {
	private String payload;
	private Object header;
	
	public String getPayload() {
		return payload;
	}

	public Object getHeader() {
		return header;
	}
	
	public void setHeader(Object header) {
		this.header = header;
	}

	public void setPayload(String encodedPayload) {
		this.payload = encodedPayload;
	}
}
