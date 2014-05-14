package edu.upenn.cis650.score;

/**
 * Models the feedback instance
 * @author bhaveshraheja
 *
 */
public class FeedbackInstance {

	private final double deltaUpdateRate = 0.9;
	
	private FeatureWeight fw;
	private double eta;
	private double delta;
	private int numberFeedback;
	private int featureVector;
	
	public FeedbackInstance() {
		fw = FeatureWeightFactory.getRandomWeights();
		eta = 1;
		delta = 2;
		numberFeedback = 0;
		featureVector = 100000;
	}
	
	
	/**
	 * Updates the current feature vector, feature weights 
	 * to reflect the ORed value of the feature vectors
	 * @param featureVector, the new feature vector
	 */
	public void addSelected(Feature selectedFeature) {
		//System.out.println("Selected Feature: "+selectedFeature);
		//1. Increment the number of feedback steps
		this.numberFeedback ++;
		
		//2. Generate the new feature vector
		System.out.println("This:"+this.featureVector+">>>that:"+selectedFeature.getFeatureVector());
		this.featureVector = bitwiseOR(this.featureVector, selectedFeature.getFeatureVector());
		System.out.println("Bitwise OR="+bitwiseOR(this.featureVector, selectedFeature.getFeatureVector())+"\nNORMAL OR="+(this.featureVector|selectedFeature.getFeatureVector()));
		
		//3. Based on update of feature vector, update the weights by an increment = learning rate * increment step
		fw.updateWeights(selectedFeature, eta*delta);
		
		//4. Update eta & delta values
		delta = 0.9*delta;
		
	}
	
	public int bitwiseOR(int x, int y) {
		
		String a = ""+x;
		String b = ""+y;
		String res = "";
		for(int i = 0; i < a.length(); i++) {
			if(a.charAt(i) == '0' && b.charAt(i)=='0')
				res = res + "0";
			else
				res = res + "1";
		}
		
		return Integer.parseInt(res);
	}
	
	@Override
	public String toString() {
		return "Feature Vector:"+featureVector+"|Feature Weights:"+fw;
	}
	
	
	//Getters and Setters
	public FeatureWeight getFeatureWeight() {
		return this.fw;
	}
	
	public double getLearningRate() {
		return eta;
	}
	
	public double getIncrementStep() {
		return delta;
	}
	
	public int getFeatureVector() {
		return this.featureVector;
	}
	
	public void setFeatureWeight(FeatureWeight fw) {
		this.fw = fw;
	}
	
	public void setLearningRate(double eta) {
		this.eta = eta;
	}
	
	public void setIncrementStep(double delta) {
		this.delta = delta;
	}
	
	public void setFeatureVector(int featureVector) {
		this.featureVector  = featureVector;
	}

}
