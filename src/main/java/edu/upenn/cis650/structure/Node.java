package edu.upenn.cis650.structure;

import java.util.HashMap;
import java.util.Map;
import edu.upenn.cis650.sources.Source;

/**
 * The structural element of a node. A node can be referred to as 
 * the smallest unit of a source that can be accessed
 * @author bhaveshraheja
 *
 */
public class Node {
	
	/**
	 * The node's text
	 */
	private String nodeText;
	
	
	/**
	 * The node's source
	 */
	private Source source;
	
	/**
	 * The features/properties associated with the node
	 */
	private Map<String, Object> featureMap;
	
	
	
	public Node(String nodeText, Source s) {
		this.nodeText = nodeText;
		this.source = s;
		featureMap = new HashMap<String, Object>();
	}
	
	public String getNodeText() {
		return this.nodeText;
	}
	
	public void setSource(Source source) {
		this.source = source;
	}
	
	public Source getSource() {
		return this.source;
	}
	
	
	/**
	 * Add a feature associated with the node
	 * @param key 
	 * The feature/property name
	 * @param value 
	 * The feature/property value
	 * @return 
	 * True if the feature/property already exists, and is overwritten
	 * False if the feature/property does not exist, and is created
	 */
	public boolean addFeature(String key, Object value) {
		
		boolean alreadyPresent = false;
		
		Object x = featureMap.get(key);
		
		if(x != null) {
			alreadyPresent = true;
		}
		
		featureMap.put(key, value);
		
		return alreadyPresent;
		
	}
	
	public Object getFeature(String key) {
		return featureMap.get(key);
	}
	
	

}
