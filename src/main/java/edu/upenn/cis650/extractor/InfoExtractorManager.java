package edu.upenn.cis650.extractor;

import edu.upenn.cis650.exception.InitializationErrorException;
import edu.upenn.cis650.exception.ProcessException;
import edu.upenn.cis650.sources.Source;
import edu.upenn.cis650.sources.SourceEdge;
import edu.upenn.cis650.sources.SourceSpaceExplorer;
import edu.upenn.cis650.structure.Association;
import edu.upenn.cis650.structure.Node;
import edu.upenn.cis650.structure.SourceAssociation;
import edu.upenn.cis650.suggestion.Suggestions;
import gate.Gate;
import gate.util.GateException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Models the several Information Extraction Controllers
 * Creates a  pool of these controllers, that are identified by source
 * @author bhaveshraheja
 *
 */
public class InfoExtractorManager {

	private static final int NEXT_LEVEL_SOURCE_EXPLORE_THRESHOLD = 5;
		
	public static void initialize() throws GateException {
		Gate.init();
	}
	
	
	/**
	 * Mapping between Source Address & Information Extractor
	 */
	private static Map<String, InfoExtractor> IEPool;
	
	
	static {
		IEPool = new HashMap<String, InfoExtractor>();
	}
	
	public static InfoExtractor getInfoExtractor(Source s) {
		if(s == null || s.getSourceAddresss() == null || s.getSourceAddresss().length() == 0)
			return null;
		InfoExtractor result = IEPool.get(s.getSourceAddresss());
		if(result != null)
			return result;
		else {
			result = new GateInfoExtractor();
			try {
				result.initialize();
				result.setSource(s);
			} catch (InitializationErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			IEPool.put(s.getSourceAddresss(), result);
			return result;		
		}
		
	}
	
	/**
	 * Using the appropriate Information Extractor for the source, recommends based 
	 * on a given node within the source.
	 * @param node, within the document
	 * @return a list of {@link edu.upenn.cis650.suggestion.Suggestion}
	 * @throws ProcessException
	 */
	public static Suggestions recommend(Node node) throws ProcessException {
		//Identify the correct Information Extractor from this node, based on its source
		InfoExtractor ie = getInfoExtractor(node.getSource());
		if(ie == null)
			throw new ProcessException("Information Extractor for Source["+node.getSource().getSourceAddresss()+"] not found");
		
		else {
			Suggestions results = ie.recommend(node);
			
			
			return results;
		}
	}
	
	public static Suggestions recommend(Association association, Node markerNode) throws ProcessException {
		
		if(association.getMarkerNode().getSource().equals(association.getValueNode().getSource())) {
			return InfoExtractorManager.getInfoExtractor(association.getMarkerNode().getSource()).recommend(association, markerNode);
		}
		else {
			//Create a new association between the Original Marker Node & the source address
			Suggestions suggestions = new Suggestions();
			List<Source> nextSources = InfoExtractorManager.getInfoExtractor(association.getMarkerNode().getSource()).getAssociatiedSource(new SourceAssociation(association.getMarkerNode(), association.getValueNode().getSource()), markerNode);
			System.out.println("Next Sources is"+nextSources);
			int count = 0;
			for(Source src: nextSources) {
				
				System.out.println("Associated Source is "+src.getSourceAddresss());
				Map<String, Object> features = InfoExtractorManager.getInfoExtractor(association.getValueNode().getSource()).getNodeFeatures(association.getValueNode());
				//Now within the next source, search for the corresponding node information based on the annotation information
				//from the association
				System.out.println(features);
				suggestions.addAll(InfoExtractorManager.getInfoExtractor(src).recommend(features));
				if(count > NEXT_LEVEL_SOURCE_EXPLORE_THRESHOLD)
					break;
				count ++;
			}
			 
			return suggestions;
			
			
		}
	}
	
	public static SourceEdge searchSourceSpace(Node startNode, String searchText) {
		InfoExtractor ie = InfoExtractorManager.getInfoExtractor(startNode.getSource());
		List<SourceEdge> sourceEdges = ie.getConnectingSources(startNode);
		for(SourceEdge se: sourceEdges)
			System.out.println(se);
		SourceSpaceExplorer sse = new SourceSpaceExplorer(sourceEdges, searchText);
		return sse.getSourceContaining();
		
	}
	
	
	
	
	

}
