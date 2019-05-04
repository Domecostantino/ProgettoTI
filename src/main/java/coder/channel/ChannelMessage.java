package coder.channel;

public class ChannelMessage {
	Object huffmanHeader=null;
	int payloadLength=0;
	byte[] payload=null;
	public ChannelMessage(Object header, int payloadLenght, byte[] payload) {
		this.huffmanHeader=header;
		this.payloadLength=payloadLenght;
		this.payload=payload;
	}
	public Object getHuffmanHeader() {
		return huffmanHeader;
	}
	public int getPayloadLength() {
		return payloadLength;
	}
	public byte[] getPayload() {
		return payload;
	}
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
}
