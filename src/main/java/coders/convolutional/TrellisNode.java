package coders.convolutional;

public class TrellisNode {
	private String state;
	private int outputValue;
	private TrellisNode successor;
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public int getOutputValue() {
		return outputValue;
	}
	public void setOutputValue(int outputValue) {
		this.outputValue = outputValue;
	}
	public TrellisNode getSuccessor() {
		return successor;
	}
	public void setSuccessor(TrellisNode successor) {
		this.successor = successor;
	}
	
	public boolean hasSuccessor() {
		return successor!=null;
	}
}
