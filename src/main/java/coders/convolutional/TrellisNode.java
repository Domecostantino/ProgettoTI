package coders.convolutional;

import java.util.LinkedList;

public class TrellisNode implements Comparable<TrellisNode> {
	private String state;
	private LinkedList<TrellisNode> successors; // il byte è l'input corrente
	private TrellisNode predecessor;

	public TrellisNode(String state, TrellisNode predecessor) {
		this.state = state;
		this.predecessor = predecessor;
		successors = new LinkedList<>();
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public LinkedList<TrellisNode> getSuccessors() {
		return successors;
	}

	public boolean hasSuccessor() {
		return successors.isEmpty();
	}

	public void generateSuccessors() {
		String partialState = state.substring(0, state.length() - 1);
		String stateSucc1 = "0" + partialState;
		String stateSucc2 = "1" + partialState;

		TrellisNode succ1 = new TrellisNode(stateSucc1, this);
		TrellisNode succ2 = new TrellisNode(stateSucc2, this);
		
		successors.add(succ1);
		successors.add(succ2);
	}
	
	@Override
	public String toString() {
		return this.state;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrellisNode other = (TrellisNode) obj;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	@Override
	public int compareTo(TrellisNode o) {
		return state.compareTo(o.state);
	}
}
