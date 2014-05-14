package edu.upenn.cis650.parsers;

import java.util.List;

import edu.upenn.cis650.sources.Source;
import edu.upenn.cis650.structure.Node;
import edu.upenn.cis650.exception.InitializationErrorException;
import edu.upenn.cis650.queries.Query;

public abstract class SourceParser {
	
	
	/**
	 * Finds the nodes matching a query, for example, Xpathquery
	 * @param query
	 * @return the list of matching nodes
	 */
	public abstract List<Node> find(Query query);
	
	/**
	 * Finds the nodes containing the given text
	 * @param text, the text to be searched
	 * @return the list of matching nodes
	 */
	public abstract List<Node> findContainingText(String text);
	
	/**
	 * Initialization method for creating the parser
	 * @param s the source over which the parser is based
	 * @throws InitializationErrorException if there was an error while initializing the object
	 */
	public abstract void createParser(Source s) throws InitializationErrorException;


}
