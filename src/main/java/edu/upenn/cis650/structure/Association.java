package edu.upenn.cis650.structure;

/**
 * Models the Marker-Value node association
 * @author bhaveshraheja
 *
 */
public class Association {
	
	private Node markerNode;
	
	private Node valueNode;
	
	public Association(Node markerNode, Node valueNode) {
		this.markerNode = markerNode;
		this.valueNode = valueNode;
	}
	
	public Node getMarkerNode() {
		return this.markerNode;
	}
	
	public Node getValueNode() {
		return this.valueNode;
	}
	
	

}
