package edu.upenn.cis650.suggestion;

import java.util.Comparator;

import edu.upenn.cis650.score.FeatureWeight;

public class SuggestionComparator implements Comparator<Suggestion> {
	
	private FeatureWeight fw;
	
	public SuggestionComparator(FeatureWeight fw) {
		
		this.fw = fw;
	}
	
	@Override
	public int compare(Suggestion o1, Suggestion o2) {
		
		return (o1.getScore(fw) < o2.getScore(fw)) ? 1: (o1.getScore(fw) > o2.getScore(fw)) ? -1: 0;
	}

	

}
