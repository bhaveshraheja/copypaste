package edu.upenn.cis650.structure;

import edu.upenn.cis650.sources.Source;

public class SourceAssociation {

	private Node markerNode;
	private Source valueSource;
	
	public SourceAssociation(Node markerNode, Source valueSource) {
		this.markerNode = markerNode;
		this.valueSource = valueSource;
	}
	
	public Node getMarkerNode() {
		return this.markerNode;
	}
	
	public Source getValueSource() {
		return this.valueSource;
	}
	

	
	

}
