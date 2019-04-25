package coders.convolutional;

import java.util.LinkedList;

public class TrellisPath {
	LinkedList<TrellisNode> path;
	int pathMetric;
	
	public TrellisPath() {
		path = new LinkedList<>();
	}
	
	public void addNode(TrellisNode node) {
		path.add(node);
	}

}
