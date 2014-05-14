package edu.upenn.cis650.suggestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.upenn.cis650.score.Feature;
import edu.upenn.cis650.score.FeatureWeight;

/**
 * A list abstract for Suggestions
 * @author bhaveshraheja
 *
 */
public class Suggestions implements Iterable<Suggestion> {
	
	private List<Suggestion> suggestions;
	
	public Suggestions() {
		suggestions = new ArrayList<Suggestion>();
	}
	
	public Suggestion get(int index) {
		return suggestions.get(index);
	}
	
	public List<Suggestion> toList() {
		return suggestions;
	}
	
	public void add(Suggestion suggestion) {
		suggestions.add(suggestion);
	}
	
	/**
	 * Normalizes all the suggestions 
	 * @param newMin, the new range start
	 * @param newMax, the new range end
	 * @param attr, the feature attribute that needs to be normalized
	 */
	public void normalize(double newMin, double newMax, short attr) {
		
		double minValue = 999;
		double maxValue = 0;
		if(attr == Feature.NODE_DISTANCE_ATTR) {
			//Find out max & min first
			for(Suggestion s: suggestions) {
				if(s.getFeature().getNodeDistance() < minValue)
					minValue = s.getFeature().getNodeDistance();
				if(s.getFeature().getNodeDistance() > maxValue)
					maxValue = s.getFeature().getNodeDistance();
			}
			if(minValue == maxValue) {
				//Ignore
			}
			else {
				double nodeDistance;
				for(Suggestion s: suggestions) {
					nodeDistance = s.getFeature().getNodeDistance();
					s.getFeature().setNodeDistance((nodeDistance - minValue)/(maxValue - minValue));
				}
			}
			
		}
		
		else if(attr == Feature.PROPERTY_SIMILARITY_ATTR) {
			//Normalize property similarity
			for(Suggestion s: suggestions) {
				if(s.getFeature().getPropertySimilarityMeasure() < minValue)
					minValue = s.getFeature().getPropertySimilarityMeasure();
				if(s.getFeature().getNodeDistance() > maxValue)
					maxValue = s.getFeature().getPropertySimilarityMeasure();
			}
			if(minValue == maxValue) {
				//Ignore
			}
			else {
				double propSimilarity;
				for(Suggestion s: suggestions) {
					propSimilarity = s.getFeature().getPropertySimilarityMeasure();
					s.getFeature().setPropertySimilarity((propSimilarity - minValue)/(maxValue-minValue) );
				}
			}
			
		}
		
		
	}
	
	public void normalizeSourceDistance() {
		double minValue = 999;
		double maxValue = 0;
			
			for(Suggestion s: suggestions) {
				if(s.getFeature().getSourceDistance() < minValue)
					minValue = s.getFeature().getSourceDistance();
				if(s.getFeature().getSourceDistance() > maxValue)
					maxValue = s.getFeature().getSourceDistance();
			}
			if(minValue == maxValue) {
				//Ignore
			}
			else {
				double sourceDistance;
				for(Suggestion s: suggestions) {
					sourceDistance = s.getFeature().getNodeDistance();
					s.getFeature().setSourceDistance((sourceDistance - minValue)/(maxValue-minValue));
				}
			}
			
			
		
	}

	@Override
	public Iterator<Suggestion> iterator() {
		return suggestions.iterator();
	}
	
	
	public void addProvenanceInfo(String prov) {
		for(Suggestion suggestion: suggestions) {
			suggestion.setProvInfo(suggestion.getProvInfo()+"<br/>"+prov);
		}
	}
	
	public void normalize(double newMin, double newMax) {
		normalize(newMin, newMax, (short) 0);
		normalize(newMin, newMax, (short) 1);
		normalize(newMin, newMax, (short) 2);
	}
	
	public void sort(FeatureWeight fw) {
		Collections.sort(suggestions, new SuggestionComparator(fw));
	}
	
	public void addAll(Suggestions suggestions) {
		this.suggestions.addAll(suggestions.toList());
	}
	

}
