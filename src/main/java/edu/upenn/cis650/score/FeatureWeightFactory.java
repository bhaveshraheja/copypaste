package edu.upenn.cis650.score;

import java.util.Random;

public class FeatureWeightFactory {

	private FeatureWeightFactory() {
		//To prevent instantiation
	}
	
	public static FeatureWeight getSingleNodeWeights() {
		
		FeatureWeight fw = new FeatureWeight();
		
		fw.setDomainMatchWeight(0);
		fw.setNodeDistanceWeight(5);
		fw.setPropertySimilarityWeight(5);
		fw.setSourceDistanceWeight(0);
		fw.setTypeMatchWeight(5);
		
		return fw;
		
	}
	
	public static FeatureWeight getPairNodeWeights() {
		FeatureWeight fw = new FeatureWeight();
		
		fw.setDomainMatchWeight(0);
		fw.setNodeDistanceWeight(5);
		fw.setPropertySimilarityWeight(5);
		fw.setSourceDistanceWeight(0);
		fw.setTypeMatchWeight(5);
		
		return fw;
		
	}
	
	
	public static FeatureWeight getPairNodeMultipleSourceWeights() {
		
		FeatureWeight fw = new FeatureWeight();
		
		fw.setDomainMatchWeight(0);
		fw.setNodeDistanceWeight(5);
		fw.setPropertySimilarityWeight(5);
		fw.setSourceDistanceWeight(5);
		fw.setTypeMatchWeight(5);
		
		return fw;
	}
	
	public static FeatureWeight getBlankWeights() {
		
		FeatureWeight fw = new FeatureWeight();
		
		fw.setDomainMatchWeight(0);
		fw.setNodeDistanceWeight(0);
		fw.setPropertySimilarityWeight(0);
		fw.setSourceDistanceWeight(0);
		fw.setTypeMatchWeight(0);
		
		return fw;
		
	}
	
	public static FeatureWeight getRandomWeights() {
		FeatureWeight fw = new FeatureWeight();
		
		Random rand = new Random();
		
		
		
		fw.setDomainMatchWeight(rand.nextDouble());
		fw.setNodeDistanceWeight(rand.nextDouble());
		fw.setPropertySimilarityWeight(rand.nextDouble());
		fw.setSourceDistanceWeight(rand.nextDouble());
		fw.setTypeMatchWeight(rand.nextDouble());
		
		return fw;
	}

}
