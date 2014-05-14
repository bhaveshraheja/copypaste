package edu.upenn.cis650.extractor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.upenn.cis650.exception.InitializationErrorException;
import edu.upenn.cis650.exception.ProcessException;
import edu.upenn.cis650.score.Feature;
import edu.upenn.cis650.sources.Source;
import edu.upenn.cis650.sources.SourceComparator;
import edu.upenn.cis650.sources.SourceEdge;
import edu.upenn.cis650.sources.URLSource;
import edu.upenn.cis650.structure.AnnotationWrapper;
import edu.upenn.cis650.structure.AnnotationWrapperComparator;
import edu.upenn.cis650.structure.Association;
import edu.upenn.cis650.structure.Node;
import edu.upenn.cis650.structure.ParentAnnotationComparator;
import edu.upenn.cis650.structure.SourceAssociation;
import edu.upenn.cis650.suggestion.Suggestion;
import edu.upenn.cis650.suggestion.Suggestions;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.DocumentContent;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.InvalidOffsetException;
import gate.util.SimpleFeatureMapImpl;

public class GateInfoExtractor extends InfoExtractor {
	
	public Document d;
	
	private Source s;
	
	private DocumentContent docContent;
	
	private AnnotationSet markupAnnotation;
	
	private static final String[] PARENT_HTML_TAGS = {"div", "section", "article", "html", "body","span","p",
													   "h1","h2","h3","h4","h5","h6"};
	/**
	 * The features to ignore while doing feature comparison
	 */
	private static final String[] IGNORE_FEATURES = {"href", "id"};
	
	
	
	@Override
	public Document getDocument() {
		
		return this.d;
	}
	
	@Override
	public void initialize() throws InitializationErrorException {
		//Initialize Gate only if it is not already initialized
		if(!Gate.isInitialised()) {
			
			//Initialize Gate
			try {
				Gate.init();
			} catch (GateException e) {
				
				e.printStackTrace();
				throw new InitializationErrorException("Error in initializing Gate");
			}
		}
	}
	
	@Override
	public void setSource(Source s) {
		this.s = s;
		try {
			if(s instanceof URLSource) {
				d = Factory.newDocument(new URL(s.getSourceAddresss()), "utf-8");
				
			}
			else {
				d = Factory.newDocument(s.getSourceContents());
			}
			
		} catch (ResourceInstantiationException e) {
			
			e.printStackTrace();
		} catch (MalformedURLException e) {
		
			e.printStackTrace();
		}	
		
		docContent = d.getContent();
		markupAnnotation =  d.getAnnotations("Original markups");
	}


	@Override
	public Suggestions recommend(Node node) throws ProcessException {		
		return recommend(findClosestMatchingAnnotation(node));
	}
	
	@Override
	public Suggestions recommend(Association association, Node markerNode) throws ProcessException {
		
		Annotation markerAnn = findClosestMatchingAnnotation(association.getMarkerNode());
		Annotation valueAnn = findClosestMatchingAnnotation(association.getValueNode());
		Annotation markerAnnotation = findClosestMatchingAnnotation(markerNode);
		return recommend(markerAnn, valueAnn, markerAnnotation);
	}
	
	/**
	 * See {@link #recommend(Node)}
	 * @param matchedAnnotation
	 * @return List<Suggestion>
	 * @throws ProcessException
	 */
	private Suggestions recommend(Annotation matchedAnnotation) throws ProcessException {
		if(matchedAnnotation == null) {
			throw new ProcessException("A matching annotation was not found");
		}
		
		List<Annotation> markerNodeParents = getAnnotationParents(matchedAnnotation);
		
		Suggestions suggestionList = new Suggestions();
		
		/**
		 * For single node recommendations, in order to save computation, return nodes that match
		 * atleast the class name or part of the class name
		 */
		String currentContent;
		Suggestion currentSuggestion;
		//Assumption TAG Name = Markup Annotation Type (true for original markups)
		double currDistance;
		String provInfo = "Source:"+this.s.getSourceAddresss();
		for(Annotation current : markupAnnotation.get(matchedAnnotation.getType())) {
		
			try {
				currentContent = docContent.getContent(current.getStartNode().getOffset(), current.getEndNode().getOffset()).toString();
				
				//Create a new feature
				Feature f = new Feature();
				
				//As the annotation type matches
				f.setTypeMatch();
				
				//Set the distance value
				currDistance = (double)Math.abs(current.getId() - matchedAnnotation.getId());
				
				f.setNodeDistance(currDistance);
				
				//Set the property Similarity Score
				f.setPropertySimilarity(getSimilarityRatio(current, matchedAnnotation));
				
				//As it belongs to the same source
				f.setSourceDistance(this.s.getDistance());
				
				
				
				//If the text content is not blank/empty, add it to the list of suggestions
				if(currentContent != null && currentContent.trim().length() > 0) {
					currentSuggestion = new Suggestion(currentContent, f);
					currentSuggestion.setProvInfo(provInfo);
					suggestionList.add(currentSuggestion);
				}	
			
			
			} 
			catch (InvalidOffsetException e) {
				e.printStackTrace();
			}	
		}
		suggestionList.normalize(0, 1);
		
		return suggestionList;
	}
	
	private double getSimilarityRatio(Annotation current, Annotation matchedAnnotation) {
		
		FeatureMap currentFeatureMap = (FeatureMap)((SimpleFeatureMapImpl)current.getFeatures()).clone();
		
		FeatureMap matchedFeatureMap = (FeatureMap)((SimpleFeatureMapImpl)matchedAnnotation.getFeatures()).clone();
		////System.out.println("Current:"+current+"\n"+"Matched:"+matchedAnnotation);
		for(String ignore: IGNORE_FEATURES) {
			currentFeatureMap.remove(ignore);
			matchedFeatureMap.remove(ignore);
		}
		
		//Now compare the number of matching features in the annotations
		int numberFeatures = currentFeatureMap.size();
		
		if(numberFeatures == 0)
			return 0;
		
		double numberCommonFeatures = 0;
		
		for(Object key: currentFeatureMap.keySet()) {
			
			if(currentFeatureMap.get(key).equals(matchedFeatureMap.get(key)))
				numberCommonFeatures++;
		}
		
		if(numberCommonFeatures == 0)
			return 0;
		
		return numberCommonFeatures/(double)numberFeatures;
	}

	/**
	 * Based on {@link #recommend(Association, Node)}, allows for modularity and abstraction
	 * @param markerAnn, the marker Annotation
	 * @param valueAnn, the value Annotation (both above marker & value based on association)
	 * @param markerAnnotation, the new marker for which the value is sought
	 * @return List<Suggestion>, a list of suggestions
	 * @throws ProcessException 
	 */
	private Suggestions recommend(Annotation markerAnn, Annotation valueAnn, Annotation markerAnnotation) throws ProcessException {
		try {
			
			Annotation lca = findLCA(markerAnn,valueAnn);
			
			//Now find reocmmendation for value nodes, and match it closest based on association's lca
			if(markerAnnotation == null) {
				throw new ProcessException("Corresponding Annotation for marker node not found");
			}
			
			List<Annotation> markerParents = getAnnotationParents(markerAnnotation); 
			Annotation matchedMarkerLCA = null;
			for(Annotation parent: markerParents) {
				if(parent.getType().equals(lca.getType())) {
					if(lca.getFeatures().get("class") != null && parent.getFeatures().get("class") != null) {
						if(parent.getFeatures().get("class").equals(lca.getFeatures().get("class"))) {
							matchedMarkerLCA = parent;
							break;
							}
					}
				}
			}
			Suggestions resultList = new Suggestions();
			//System.out.println("Matched marker LCA ="+matchedMarkerLCA);
			//Initially all annotations are conteding annotations
			AnnotationSet contendingAnnotations = markupAnnotation;
			
			if(matchedMarkerLCA != null) {
				//Filter the nodes contained in the matching LCA if any
				contendingAnnotations = markupAnnotation.getContained(matchedMarkerLCA.getStartNode().getOffset(), matchedMarkerLCA.getEndNode().getOffset());
			}
			
			//Now filter via the tag type & features
			Suggestion suggest;
			String content;
			double markerDistance = Math.abs(markerAnn.getId() - valueAnn.getId());
			double newDistance;
			String provInfo = "Source:"+this.s.getSourceAddresss();
			for(Annotation ann: contendingAnnotations.get(valueAnn.getType())) {
				//Create the suggestion
				////System.out.println(ann);
				content = docContent.getContent(ann.getStartNode().getOffset(), ann.getEndNode().getOffset()).toString();
				if(content.length() == 0) {
					continue;
				}
				
				//Create  a new feature
				Feature f = new Feature();
				
				//Set Type match as annotation types match
				f.setTypeMatch();
				
				
				//Difference between the [(Predicted Value Node - New Marker Node)-(Original marker node-value Node)]
				newDistance = Math.abs(ann.getId() - markerAnnotation.getId());
				f.setNodeDistance(Math.abs(newDistance - markerDistance));
				///As it is the same source
				f.setSourceDistance(this.s.getDistance());
				
				f.setPropertySimilarity(getSimilarityRatio(ann, valueAnn));
				
				
				
				suggest = new Suggestion(content,f);
				suggest.setProvInfo(provInfo);
				resultList.add(suggest);
				
			}
			resultList.normalize(0, 1);
			return resultList;
			
			//Now find the corresponding path from the LCA to the child node
			
		} 
		catch (InvalidOffsetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Returns the Lowest Common Ancestor of both the nodes
	 * @param association
	 * @return
	 * @throws ProcessException 
	 */
	private Annotation findLCA(Annotation markerNode, Annotation valueNode) throws ProcessException {
		
		//First find the closest approximation to both the nodes
		Annotation one = markerNode;
		Annotation two = valueNode;
		
		if(one == null || two == null) {
			throw new ProcessException("Closest matching Annotations not found within document");
		}
		
		List<Annotation> parentsOne = getAnnotationParents(one);
		
		List<Annotation> parentsTwo = getAnnotationParents(two);
		
		
		//Trace down the parent list till their parents are different
		Annotation commonParent = null;
		int index = 0;
		while(index < parentsOne.size() && index < parentsTwo.size() && (parentsOne.get(index).getId() == parentsTwo.get(index).getId())) {
			
			commonParent = parentsOne.get(index);
			
			index ++;
		}
		
		//Now common parent is the lca
		return commonParent;
	}
	
	@Override
	public List<Source> getAssociatiedSource(SourceAssociation association, Node markerNode) throws ProcessException {
		Annotation sourceAdd = null;
		Annotation markerAnn = findClosestMatchingAnnotation(association.getMarkerNode());
		Annotation markerAnnotation = findClosestMatchingAnnotation(markerNode);
		List<AnnotationWrapper> annWrappers = new ArrayList<AnnotationWrapper>();
		
		//For the alternative ways of the source address, find the corresponding matching annotations along
		//with distance from the original Marker Annotaiton (markerAnn) and combine it in a list
		for(String sourceRef: association.getValueSource().getAlternativeAddresses()) {
			//System.out.println("Source Ref"+sourceRef);
			annWrappers.addAll(findCorrespondingAnnotation(sourceRef, markerAnn.getId()));
		}
		if(annWrappers == null || annWrappers.size() == 0)
			throw new ProcessException("Matching Source Annotaiton not found");
		
		Collections.sort(annWrappers, new AnnotationWrapperComparator());
		
		sourceAdd = annWrappers.get(0).getAnnotation();
		
		
		//Now recommend a new sourceAddress based on this for the corresponding marker node
		
		
		Annotation lca = findLCA(markerAnn,sourceAdd);
		
		//Now find reocmmendation for value nodes, and match it closest based on association's lca
		if(markerAnnotation == null) {
			throw new ProcessException("Corresponding Annotation for marker node not found");
		}
		
		List<Annotation> markerParents = getAnnotationParents(markerAnnotation); 
		Annotation matchedMarkerLCA = null;
		for(Annotation parent: markerParents) {
			if(parent.getType().equals(lca.getType())) {
				if(lca.getFeatures().get("class") != null && parent.getFeatures().get("class") != null) {
					if(parent.getFeatures().get("class").equals(lca.getFeatures().get("class"))) {
						matchedMarkerLCA = parent;
						break;
						}
				}
			}
		}
	
		//Initially all annotations are conteding annotations
		AnnotationSet contendingAnnotations = markupAnnotation;
		
		if(matchedMarkerLCA != null) {
			//Filter the nodes contained in the matching LCA if any
			contendingAnnotations = markupAnnotation.getContained(matchedMarkerLCA.getStartNode().getOffset(), matchedMarkerLCA.getEndNode().getOffset());
		}
		
		Annotation resultAnn = null;
		int idDifference = 99999;
		String href;
		FeatureMap sourceFeatureMap = (FeatureMap)((SimpleFeatureMapImpl)sourceAdd.getFeatures()).clone();
		sourceFeatureMap.remove("href");
		int frontDir = (markerAnn.getId() > sourceAdd.getId()) ? 1 : (markerAnn.getId() == sourceAdd.getId()) ? 0: -1;
		List<Source> possibleSources = new ArrayList<Source>();
		Source currSource;
		for(Annotation ann: contendingAnnotations.get("a", sourceFeatureMap)) {
			href = (String)ann.getFeatures().get("href");
			//System.out.println("Possible associated source is "+ann);
			if(href != null) {
				switch(frontDir) {
					case 1:
						if(markerAnnotation.getId() - ann.getId() < idDifference) {
							idDifference = markerAnnotation.getId() - ann.getId();
							resultAnn = ann;
						}
						break;
						
					case -1:
						if(ann.getId() - markerAnnotation.getId() < idDifference) {
							idDifference = ann.getId() - markerAnnotation.getId();
							resultAnn = ann;
						}
						break;
					
					case 0:
						if(Math.abs(ann.getId()-markerAnnotation.getId()) < idDifference) {
							idDifference = Math.abs(ann.getId()-markerAnnotation.getId());
							resultAnn = ann;
						}
						break;
				}
				try {
					currSource= new URLSource(transformAddress((String)resultAnn.getFeatures().get("href")));
					currSource.setDistance(idDifference);
					possibleSources.add(currSource);
				} catch (InitializationErrorException e) {
					// TODO Auto-generated catch block
					//System.out.println("Dont do anything, exception was found for "+resultAnn);
				}
				
					
			}
		}
		
		
		Collections.sort(possibleSources, new SourceComparator());
		
		return possibleSources;
				
		
	}
	
	@Override
	public Map<String,Object> getNodeFeatures(Node node) {
		Map<String, Object> features = new HashMap<String, Object>();
		Annotation ann = findClosestMatchingAnnotation(node);
		
		features.put("Annotation", ann);
		//System.out.println(ann);
		features.put("Distance", ann.getId());
		if(ann.getFeatures().get("class") != null)
			features.put("Class", ann.getFeatures().get("class"));
		
		return features;
	}
	
	@Override
	public Suggestions recommend(Map<String, Object> features)  {
		Annotation ann = (Annotation) features.get("Annotation");
		Suggestions suggestions = new Suggestions();
		AnnotationSet contendingAnnotations;
		String provInfo = "Source:"+this.s.getSourceAddresss();
		if(ann != null) {
			FeatureMap f = (FeatureMap)((SimpleFeatureMapImpl)ann.getFeatures()).clone();
			for(String ignore: IGNORE_FEATURES)
				f.remove(ignore);
			contendingAnnotations = markupAnnotation.get(ann.getType(),f);
			//System.out.println(contendingAnnotations.size());
			String content;
			//System.out.println(this.s.getSourceAddresss());
			for(Annotation annotation: contendingAnnotations) {
				try {
					//System.out.println(annotation);
					content = docContent.getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString();
					if(content.length() == 0) {
						continue;
					}
					Feature feature = new Feature();
					feature.setTypeMatch();
					
					feature.setSourceDistance(this.s.getDistance());
					
					feature.setPropertySimilarity(1.0);
					
					Suggestion sg = new Suggestion(content, feature);
					sg.setProvInfo(provInfo);
					sg.setFeature(feature);
				}
				catch(InvalidOffsetException e) {
					//Ignore
				}
			}
			return suggestions;
			
		}
		
		//For the case of when annotation(ann) is null, code needs to be written
		return null;
	}
	
	
	/**
	 * Converts a relative address to a full address
	 * @param address, the address that may be relative or full
	 * @return, the full address
	 */
	private String transformAddress(String address) {
		if(address == null)
			return null;
		if(address.startsWith("http"))
			return address;
		else {
			try {
				URL x = new URL(this.s.getSourceAddresss());
				//System.out.println("x is"+x.getHost()+"|address is "+address);
				
				return "http://"+x.getHost() +  address;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * Given some text in the source, find the corresponding annotation associated with it.
	 * In case of ambiguity, return the first one
	 * @param text, the text to be searched in the document
	 * @return, the annotation
	 */
	private List<AnnotationWrapper> findCorrespondingAnnotation(String text, int markerAnnId) {
		
		/* If text is a URL, this can be trickier, as the URL is not a part of the content
		*  of the document, and is a part of the features of the document
		**/
		List<AnnotationWrapper> resultAnnotations = new ArrayList<AnnotationWrapper>();
		String href;
		//ASSUMPTION, ALL LINKS ARE USED IN <A HREF TAGS>
		for(Annotation x: markupAnnotation.get("a")) {
			href = (String) x.getFeatures().get("href");
			if(href != null && href.equals(text)) {
				resultAnnotations.add(new AnnotationWrapper(x,markerAnnId - x.getId()));
			}
			
		}
		//If nothing is found return null
		return resultAnnotations;
	}

	
	private Pattern generateRegexPattern(String className) {
		//Splits the String on spaces, and then generates a matching regular expression
		
		//Create Regular Expression of the Class Name 
		String pattern = "";
		for(String temp: className.trim().split(" ")) {
			pattern+=temp+"|";
		}
		
		return Pattern.compile(pattern);
		
	}

	
	
	/**
	 * Generates a list of the parents based on overlapping annotation IDs given a tag name
	 * to search for
	 * @param x, the annotation
	 * @return the list of parents
	 */
	private List<Annotation> getAnnotationParentsByTagName(Annotation x, String tagName) {
		
		AnnotationSet parentSet = markupAnnotation.getCovering(tagName, x.getStartNode().getOffset(), x.getEndNode().getOffset());
		
		List<Annotation> parentList = new ArrayList<Annotation>();
		for(Annotation x1: parentSet) {
			parentList.add(x1);
		}
		
		return parentList;
		
	}
	
	/**
	 * Returns the number of common parents given the tag Name
	 * @param x annotation
	 * @param y annotation
	 * @param tagName usually div or tagName of which parents we are looking at
	 * @return the integral number of common parents
	 */
	private int numberCommonParents(Annotation x, Annotation y) {
		//Returns the number of common parents between the two annotations
		
		int numberCommonParents = 0;
		List<Annotation> parentsOne = getAnnotationParents(x);
		List<Annotation> parentsTwo = getAnnotationParents(y);
		try {
			while(true) {
				if(parentsOne.get(numberCommonParents).equals(parentsTwo.get(numberCommonParents))) {
					numberCommonParents ++;			
				}
				else {
					break;
				}
			}
		}
		catch(IndexOutOfBoundsException e) {
			//Ignore if there is an exception
			e.printStackTrace();
		}
		return numberCommonParents;
	}
	
	/**
	 * Return a list of parents based on common HTML tags (@see {@link #PARENT_HTML_TAGS})
	 * @param x, the annotation
	 * @return a list of parents
	 */
	private List<Annotation> getAnnotationParents(Annotation x) {
		
		List<Annotation> parentList = new ArrayList<Annotation>();
		
		for(String parent: PARENT_HTML_TAGS) {
			parentList.addAll(getAnnotationParentsByTagName(x,parent));
		}
		
		Collections.sort(parentList, new ParentAnnotationComparator());
		
		return parentList;
	}
	
	/**
	 * Returns a list of parents based on a list of tags
	 * @param x the annotaiton
	 * @param tagList the list of tags (@see {@link #PARENT_HTML_TAGS} for reference)
	 * @return the list of parents
	 */
	private List<Annotation> getAnnotationParents(Annotation x, List<String> tagList) {
		List<Annotation> parentList = new ArrayList<Annotation>();
		for(String parent: tagList) {
			parentList.addAll(getAnnotationParentsByTagName(x,parent));
		}
		Collections.sort(parentList, new ParentAnnotationComparator());
		return parentList;
	}
	
	/**
	 * Returns the ratio of Number of Common Parents/ Total Number of Parents 
	 * @param current the current node
	 * @param parentList the list of parents against which the comparison is made
	 * @return the ratio
	 */
	private double commonParentsRatio(Annotation current, List<Annotation> parentList) {
		List<Annotation> currentParents = getAnnotationParents(current);
		int numberCommonParents = 0;
		int numberParents = parentList.size();
		
		if(numberParents == 0) {
			return 0;
		}
		
		try {
			while(true) {
				if(currentParents.get(numberCommonParents).equals(parentList.get(numberCommonParents))) {
					numberCommonParents ++;
				}
				else {
					break;
				}
			}
		}
		catch(IndexOutOfBoundsException e) {
			//Ignore the exception
			
		}
		
		return (double)numberCommonParents / (double)numberParents;
	}
	
	/**
	 * Find the closest match to an Annotation given a node based on its
	 * (a) text content
	 * (b) tag
	 * (c) class name, if any
	 * @param node, the node to be matched against
	 * @return the closest matched Annotation
	 */
	private Annotation findClosestMatchingAnnotation(Node node) {
		
		//Find all occurrences of the string in the document
		String content = docContent.toString();
		Pattern textPattern = Pattern.compile(escapeMetaSequences(node.getNodeText()));
		
		Matcher matcher = textPattern.matcher(content);
		
		long start, end;
		AnnotationSet currentPossibilities = null;
		List<Annotation> possibilities = new ArrayList<Annotation>();
		String tagName, className = null;
		while(matcher.find()) {
			 
			start = matcher.start();
			end = start + node.getNodeText().length();
			////System.out.println(node.getNodeText()); 
			currentPossibilities = markupAnnotation.getContained(start, end);
			tagName = (String) node.getFeature("tag").toString();
			className = (String) node.getFeature("class").toString();
			////System.out.println(("Start:"+start+"|End:"+end+"|Matching Possibilities-----"+currentPossibilities)); 
			if(tagName != null) {
				currentPossibilities = currentPossibilities.get(tagName);
			}
			
			//Add the possibilities to a list 
			possibilities.addAll(currentPossibilities);
		
		}
		
		
		if(className != null) {
			List<Annotation> tempList = new ArrayList<Annotation>();
			for(Annotation possibility: possibilities) {
				//Filter by class Name
				if(possibility.getFeatures().get("class") != null && possibility.getFeatures().get("class").equals(className)) {
					return possibility;
				}
			}
			if(tempList.size() > 0) {
				possibilities = tempList;
			}
		}
		
		//Now return the first element of possibilities
		if(possibilities.size() > 0) {
			return possibilities.get(0);
		}
		else {
			return null;
		}
	}
	
	/**
	 * For the purposes of Regex, replace characters, like
	 * '(' with '\('
	 * ')' with '\)'
	 * @param text, the unescaped string
	 * @return the string with special characters replaced
	 */
	private String escapeMetaSequences(String text) {
		return text.replace("(", "\\(")
					.replace(")","\\)")
					.replace("[", "\\[")
					.replace("]", "\\]");
	}
	
	
	private List<SourceEdge> getConnectingSources(int start) {
		
		//Assumption: All external links are of the type <a>
		AnnotationSet connectingAnnotations = markupAnnotation.get("a");
		//System.out.println("Get Connecting Soruces: "+connectingAnnotations.size());
		String content = null;
		String address = null;
		List<SourceEdge> sourceEdges = new ArrayList<SourceEdge>();
		//From these, convert these to true URLs, and create sourceedges for the same
		for(Annotation connectingAnn: connectingAnnotations) {
			//System.out.println("ANN:"+connectingAnn);
			content = (String) connectingAnn.getFeatures().get("href");
			//System.out.println("Content is"+content);
			if(content == null || content.length() == 0) {
				continue;
			}
			
			address = transformAddress(content);
			//System.out.println("Address:"+address);
			if(address != null) {
				try {
					sourceEdges.add((new SourceEdge(new URLSource(address), Math.abs(connectingAnn.getId().intValue()-start))));
				} catch (InitializationErrorException e) {
					//Ignore Exception
					e.printStackTrace();
				}
				
			}
			
		}
		return sourceEdges;
		
	}

	@Override
	public List<SourceEdge> getConnectingSources(Node node) {
		Annotation ann = findClosestMatchingAnnotation(node);
		if(ann != null)
			return getConnectingSources(ann.getId());
		else
			return null;
	}

	@Override
	public String getSourceContent() {
		return this.docContent.toString();
	}
	
	
	
	

}
