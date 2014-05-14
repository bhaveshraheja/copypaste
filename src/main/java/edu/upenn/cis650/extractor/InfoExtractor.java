package edu.upenn.cis650.extractor;

import java.util.List;
import java.util.Map;

import edu.upenn.cis650.exception.InitializationErrorException;
import edu.upenn.cis650.exception.ProcessException;
import edu.upenn.cis650.sources.Source;
import edu.upenn.cis650.sources.SourceEdge;
import edu.upenn.cis650.structure.Association;
import edu.upenn.cis650.structure.Node;
import edu.upenn.cis650.structure.SourceAssociation;
import edu.upenn.cis650.suggestion.Suggestion;
import edu.upenn.cis650.suggestion.Suggestions;
import gate.Document;
import gate.util.InvalidOffsetException;

/**
 * An Information Extractor creates annotations & maintains structure
 * of the document. 
 * @author bhaveshraheja
 *
 */
public abstract class InfoExtractor {
	
	public abstract void initialize() throws InitializationErrorException;
	
	/**
	 * Provides a list of matching recommendations based on the node
	 * @param node
	 * @return the list of suggestions
	 * @throws ProcessException 
	 */
	public abstract Suggestions recommend(Node node) throws ProcessException;
	
	/**
	 * Provides a list of matching value-node recommendations based on the association
	 * and the marker node provide
	 * @param association, the association of marker-value which we are trying to model
	 * @param markerNode, the marker node against which a new recommendation for a value node is required
	 * @return
	 */
	public abstract Suggestions recommend(Association association, Node markerNode) throws ProcessException;
	
	/**
	 * Given a source association along with the node in the current source
	 * it finds the corresponding location of the new source for the document
	 * @param association
	 * @param markerNode
	 * @return the new associated source
	 * @throws ProcessException 
	 */
	public abstract List<Source> getAssociatiedSource(SourceAssociation association, Node markerNode) throws ProcessException;
	
	/**
	 * Given a node, it finds the associated features of the node
	 * with respect to the source
	 * @param node
	 * @return
	 */
	public abstract Map<String,Object> getNodeFeatures(Node node);
	
	/**
	 * Given a set of features, find the matching nodes within
	 * the current source
	 * @throws InvalidOffsetException 
	 */
	public abstract Suggestions recommend(Map<String, Object> features); 
	
	
	
	/**
	 * Sets the source(Document, URL) see {@link edu.upenn.cis650.sources.Source}
	 * @param s, Source
	 */
	public abstract void setSource(Source s);
	
	
	/**
	 * Hack method, for debugging only
	 * @return 
	 * 
	 */
	public abstract Document getDocument(); 
	
	/**
	 * For text sources only
	 * @return, the text-content if the source is textual
	 * else, returns annotated sub-content
	 */
	public abstract String getSourceContent();
	
	
	/**
	 * Returns the outer set of connecting sources
	 * @return List<SourceEdge> which holds information about the Source as well as a metric for the source
	 */
	public abstract List<SourceEdge> getConnectingSources(Node node);
	
}
