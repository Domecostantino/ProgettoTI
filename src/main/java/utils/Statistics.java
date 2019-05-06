package utils;

public class Statistics {
	
	private long initialTime;
	private long channelCodingTime;
	private long sourceCodingTime;
	private long sourceDecodingTime;
	private long channelDecodingTime;
	private double entropy;
	
	public long getSourceCodingTime() {
		return sourceCodingTime-initialTime;
	}//
	
	public long getChannelCodingTime() {
		return channelCodingTime-sourceCodingTime;
	}//
	
	public long getChannelDecodingTime() {
		return channelDecodingTime-channelCodingTime;
	}//
	
	
	
	public long getSourceDecodingTime() {
		return sourceDecodingTime-channelDecodingTime;
	}//
	
	public void setInitialTime(long initialTime) {
		this.initialTime = initialTime;
	}

	public void setSourceCodingTime(long sourceCodingTime) {
		this.sourceCodingTime = sourceCodingTime;
	}

	public void setChannelCodingTime(long channelCodingTime) {
		this.channelCodingTime = channelCodingTime;
	}

	public void setSourceDecodingTime(long sourceDecodingTime) {
		this.sourceDecodingTime = sourceDecodingTime;
	}

	public void setChannelDecodingTime(long channelDecodingTime) {
		this.channelDecodingTime = channelDecodingTime;
	}

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	
	public Statistics() {}
	
	
}//Statistics
