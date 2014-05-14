package edu.upenn.cis650.score;

public class FeatureWeight {

	
	private double typeMatchWt;
	private double domainMatchWt;
	private double sourceDistanceWt;
	private double nodeDistanceWt;
	private double propSimilarityWt;
	
	
	public FeatureWeight() {
		this.typeMatchWt = 0;
		this.domainMatchWt = 0;
		this.sourceDistanceWt = 0;
		this.nodeDistanceWt = 0;
		this.propSimilarityWt = 0;
	}
	
	public FeatureWeight(double typeMatchWeight, double domainMatchWeight, double sourceDistanceWeight, double nodeDistanceWeight, double propertySimilarityWeight) {
		this.typeMatchWt = typeMatchWeight;
		this.domainMatchWt = domainMatchWeight;
		this.sourceDistanceWt = sourceDistanceWeight;
		this.nodeDistanceWt = nodeDistanceWeight;
		this.propSimilarityWt = propertySimilarityWeight;
	}
	
	
	public void updateWeights(Feature f, double eta) {
		
		
		
		if(f.DomainMatchBitSet())
			this.domainMatchWt += eta * matchUpdateFunction();
		
		if(f.SourceDistanceBitSet())
			this.sourceDistanceWt += eta * numericUpdateFunction(1-f.getSourceDistance());
		
		if(f.TypeMatchBitSet())
			this.typeMatchWt += eta * matchUpdateFunction();
		
		if(f.NodeDistanceBitSet()) 
			this.nodeDistanceWt += eta * numericUpdateFunction(1-f.getNodeDistance());
		
		if(f.PropertySimilarityBetSet())
			this.propSimilarityWt += eta * numericUpdateFunction(f.getPropertySimilarityMeasure());
	}
	
	@Override
	public String toString() {
		return "{"+this.sourceDistanceWt+","+this.nodeDistanceWt+","+this.propSimilarityWt+","+this.typeMatchWt+","+this.domainMatchWt+"}";
	}
	
	public void updateWeights(int featureVector, double increment) {
		//Convert feature vector to string, for ease of use
		String fv = "" + featureVector;
		
		/* VERY VERY INEFFECIENT & WRONG METHOD TO UPDATE WEIGHTS
		 * 	bitDomainMatch 
		 *  bitTypeMatch 
		 *	bitNodeDistance 
		 *	bitSourceDistance
		 *	bitPropertySimilarity
		 */
		if(fv.charAt(1) == '1')
			this.domainMatchWt += increment;
		
		if(fv.charAt(2) == '1')
			this.typeMatchWt += increment;
		
		if(fv.charAt(3) == '1')
			this.domainMatchWt += increment;
		
		if(fv.charAt(4) == '1')
			this.sourceDistanceWt += increment;
		
		if(fv.charAt(5) == '1')
			this.propSimilarityWt += increment;
		
	}
	
	
	
	public double numericUpdateFunction(double x) {
		//Identity function for now
		return x;
	}
	
	public double matchUpdateFunction() {
		//Identity function for now
		return 0.5;
	}
	
	
	
	
	/** GETTERS and SETTERS **/
	public double getTypeMatchWeight() {
		return this.typeMatchWt;
	}
	
	public double getDomainMatchWeight() {
		return this.domainMatchWt;
	}
	
	public double getSourceDistanceWeight() {
		return this.sourceDistanceWt;
	}
	
	public double getNodeDistanceWeight() {
		return this.nodeDistanceWt;
	}
	
	public double getProertySimilarityWeight() {
		return this.propSimilarityWt;
	}
	
	public void setTypeMatchWeight(double weight) {
		this.typeMatchWt = weight;
	}
	
	public void setDomainMatchWeight(double weight) {
		this.domainMatchWt = weight;
	}
	
	public void setSourceDistanceWeight(double weight) {
		this.sourceDistanceWt = weight;
	}
	
	public void setNodeDistanceWeight(double weight) {
		this.nodeDistanceWt = weight;
	}
	
	public void setPropertySimilarityWeight(double weight) {
		this.propSimilarityWt = weight;
	}
	

}
