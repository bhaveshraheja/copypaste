package edu.upenn.cis650.sources;

import java.util.List;

public abstract class Source {
	
	private String sourceAddress;
	
	/**
	 * Only to be used for small sized sources, like a page URL etc
	 */
	private String sourceContent;
	
	private int distance;
	
	public Source() {
		
	}
	
	public int getDistance() {
		return this.distance;
	}
	
	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	public Source(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}
	
	public String getSourceAddresss() {
		return this.sourceAddress;
	}
	
	
	public String getSourceContents() {
		return this.sourceContent;
	}
	
	public void setSourceContents(String sourceContent) {
		this.sourceContent = sourceContent;
	}
	
	/**
	 * Provides alternative ways to textually representing the given source
	 * For example, for a URL, it will be (a) a relative URL (b) the entire URL itself
	 * Does include the original source address also
	 * @return List<String> the list of alternative source addresses for the current source
	 */
	public abstract List<String> getAlternativeAddresses();

}
