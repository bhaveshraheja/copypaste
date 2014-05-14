package edu.upenn.cis650.sources;


public class SourceEdge implements Comparable<SourceEdge> {

	private Source source;
	
	/**
	 * Using a distance metric
	 */
	private int distance;
	
	public SourceEdge() {
		
	}
	
	public SourceEdge(Source source, int distance) {
		this.source = source;
		this.distance = distance;
	}
	
	public Source getSource() {
		return this.source;
	}
	
	public int getDistance() {
		return this.distance;
	}

	
	
	@Override
	public String toString() {
		return "["+source.getSourceAddresss()+"|Distance:"+distance+"]";
	}

	@Override
	public int compareTo(SourceEdge o) {
		
		return (this.getDistance() > o.getDistance()) ? 1 : (this.getDistance() < o.getDistance()) ? -1 : 0;
	}

}
