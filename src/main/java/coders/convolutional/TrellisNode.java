package coders.convolutional;

import java.util.HashMap;

public class TrellisNode {
	private String state;
	private HashMap<Byte, TrellisNode> successors; //il byte è l'input corrente
	
	public TrellisNode(String state) {
		this.state = state;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	public HashMap<Byte, TrellisNode> getSuccessors() {
		return successors;
	}
	public void setSuccessors(HashMap<Byte, TrellisNode> successors) {
		this.successors = successors;
	}
	public boolean hasSuccessor() {
		return successors.isEmpty();
	}
}
