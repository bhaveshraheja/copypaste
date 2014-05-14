package edu.upenn.cis650.score;

public class Feature  {

	private static final double NODE_DISTANCE_STEP = 0.5;
	private static final double SOURCE_DISTANCE_STEP = 0.5;
	private static final double PROPERTY_SIMILARITY_STEP = 0.5;
	
	public static final short NODE_DISTANCE_ATTR = 0;
	public static final short SOURCE_DISTANCE_ATTR = 1;
	public static final short PROPERTY_SIMILARITY_ATTR = 2;
	
	public Feature() {
		
		setDefaults();
	}
	
	public void setDefaults() {
		this.typeMatch = false;
		this.domainMatch = false;
		this.nodeDistance = 0;
		this.sourceDistance = 0;
		this.propertySimilarityMeasure = 0;
		this.bitDomainMatch = 0;
		this.bitTypeMatch = 0;
		this.bitSourceDistance = 0;
		this.bitNodeDistance = 0;
		this.bitPropertySimilarity = 0;
		
	}
	
	
	/**
	 * Returns the feature Vector
	 * @return
	 */
	public int getFeatureVector() {
		
		String feaVec = "";
		//Each bit vector starts with 1 by default. This is to prevent 0 wrapping by integers, therefore 00000 will be 10000 and not just 0
		feaVec = "1"+ bitDomainMatch + "" 
				+ bitTypeMatch + "" 
				+ bitNodeDistance + "" 
				+ bitSourceDistance + ""
				+ bitPropertySimilarity;
		
		return Integer.parseInt(feaVec);
	}
	
	@Override
	public String toString() {
		return this.getFeatureVector()+
				"|Domain"+ this.bitDomainMatch+
				"|Type:"+this.bitTypeMatch+
				"|Source:"+this.bitSourceDistance+","+this.sourceDistance+
				"|Node:"+this.bitNodeDistance+","+this.nodeDistance+
				"|Similarity"+this.bitPropertySimilarity+","+this.propertySimilarityMeasure;
	}
	
	
	public double computeValueWiseScore(FeatureWeight fw) {
		
		double res = 0;
		
		res += this.bitDomainMatch 				* 	fw.getDomainMatchWeight()
				+ this.bitTypeMatch				* 	fw.getTypeMatchWeight()
				+ (1-this.nodeDistance)				*	fw.getNodeDistanceWeight()
				+ (1-this.sourceDistance) 			*	fw.getSourceDistanceWeight()
				+ this.propertySimilarityMeasure * fw.getProertySimilarityWeight();
		return res;
	}
	
	public double computeBitWiseScore(FeatureWeight fw){
		
		double res = 0;
		
		res += this.bitDomainMatch				*	fw.getDomainMatchWeight()
				+ this.bitNodeDistance			*	fw.getNodeDistanceWeight()
				+ this.bitPropertySimilarity	*	fw.getProertySimilarityWeight()
				+ this.bitSourceDistance		*	fw.getSourceDistanceWeight()
				+this.bitTypeMatch				*	fw.getTypeMatchWeight();
		
		return res;
	}
		
	public int getNumberBitsSet() {
		return this.bitDomainMatch + this.bitNodeDistance + this.bitPropertySimilarity + this.bitPropertySimilarity + this.bitTypeMatch;
	}
	
	
	/** TYPE MATCH **/
	
	/**
	 * 
	 * @param value, true if annotation types match, false otherwise, default: false 
	 */
	public void setTypeMatch(boolean value) {
		this.typeMatch = value;
		this.bitTypeMatch = (value) ? 1 : 0;
	}
	
	/**
	 * Sets the type match feature to true. By default its false. Set it to true if the annotation types match
	 */
	public void setTypeMatch() {
		this.setTypeMatch(true);
	}
	
	/**
	 * 
	 * @return boolean based on whether the type match feature is set or not
	 */
	public boolean getTypeMatch() {
		return this.typeMatch;
	}
	
	
	/** DOMAIN MATCH ***/
	
	public void setDomainMatch(boolean value) {
		this.domainMatch = value;
		this.bitDomainMatch = (value) ? 1: 0;
		
	}
	
	
	public void setDomainMatch() {
		setDomainMatch(true);
	}
	
	public boolean getDomainMatch() {
		return this.domainMatch;
	}
	
	
	
	/** NODE DISTANCE **/
	
	public void setNodeDistance(double distance) {
		this.nodeDistance = distance;
		this.bitNodeDistance = (distance >= NODE_DISTANCE_STEP) ? 0: 1;
		
	}
	
	public double getNodeDistance() {
		return this.nodeDistance;
	}
	
	/** SOURCE DISTANCE **/
	
	public void setSourceDistance(double distance) {
		this.sourceDistance = distance;
		this.bitSourceDistance = (distance >= SOURCE_DISTANCE_STEP) ? 0: 1;
		
	}
	
	public double getSourceDistance() {
		return this.sourceDistance;
	}
	
	/** PROPERTY SIMILARITY DISTANCE **/
	
	public void setPropertySimilarity(double similarityRatio) {
		this.propertySimilarityMeasure = similarityRatio;
		this.bitPropertySimilarity = (similarityRatio >= PROPERTY_SIMILARITY_STEP) ? 1:0;
		
	}
	
	public double getPropertySimilarityMeasure() {
		return this.propertySimilarityMeasure;
	}
	
	
	/**
	 * True if the domain matches
	 */
	private boolean domainMatch;

	
	/**
	 * True if the type of annotation/tag matches
	 */
	private boolean typeMatch;
	
	/**
	 * A measure of distance of the node within the source
	 * Min: 0 Max: 1
	 */
	private double nodeDistance;
	
	/**
	 * A measure of distance between the sources, zero if it belongs to the same source
	 * Normalized distances
	 * Min: 0 Max: 1
	 */
	private double sourceDistance;
	
	
	/**
	 * A measure of how similar the properties are of the nodes
	 * Min: 0, Max: 1
	 */
	private double propertySimilarityMeasure;
	
	
	private int bitTypeMatch;
	private int bitDomainMatch;
	private int bitNodeDistance;
	private int bitSourceDistance;
	private int bitPropertySimilarity;
	
	//GETTERS for the bits
	public boolean TypeMatchBitSet() {
		return (bitTypeMatch == 1);
	}
	
	public boolean DomainMatchBitSet() {
		return (bitDomainMatch == 1);
	}
	
	public boolean NodeDistanceBitSet() {
		return (bitNodeDistance == 1);
	}
	
	public boolean SourceDistanceBitSet() {
		return (bitSourceDistance == 1);
	}

	public boolean PropertySimilarityBetSet() {
		return (bitPropertySimilarity == 1);
	}
	

	

}
