package edu.upenn.cis650.suggestion;





import edu.upenn.cis650.score.Feature;
import edu.upenn.cis650.score.FeatureWeight;

/**
 * Models the recommendation/suggestion
 * 
 * @author bhaveshraheja
 *
 */
public class Suggestion {
	
	private String suggestionText;
	
	private Feature feature;
	
	private String provenanceInformation;
	
	/**
	 * Only for printing/display/logging purposes, always score must be recomputed from the Feature
	 */
	private double scoreValue;
	
	
	public Suggestion(String text) {
		this.suggestionText = text;
	}
	
	
	public Suggestion(String text, Feature feature) {
		this.suggestionText = text;
		this.feature = feature;
	}
	
	
	public double getScore(FeatureWeight fw) {
		scoreValue = this.feature.computeValueWiseScore(fw);
		return this.feature.computeValueWiseScore(fw);
	}	
	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public Feature getFeature() {
		return this.feature;
	}
	
	
	public String getSuggestionText() {
		return this.suggestionText;
	}
	
	@Override
	public String toString() {
		return "Text:"+this.suggestionText +"|Feature-->"+this.getFeature()+"|-|Score:"+this.scoreValue;
	}

	
	public String getProvInfo() {
		return this.provenanceInformation;
	}
	
	public void setProvInfo(String prov) {
		this.provenanceInformation = prov;
	}

	



}
