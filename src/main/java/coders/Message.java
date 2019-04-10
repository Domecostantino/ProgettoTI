package coders;

/*
 * TEMPORANEO - bisogna decidere come formalizzare un header che vada bene per tutte le codifiche di sorgente
 */

public class Message {
	private String payload;
	private Object header;

	public void setHeader(Object header) {
		this.header = header;
	}
}
