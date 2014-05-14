package edu.upenn.cis650.parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.upenn.cis650.exception.InitializationErrorException;
import edu.upenn.cis650.queries.Query;
import edu.upenn.cis650.sources.Source;
import edu.upenn.cis650.sources.URLSource;
import edu.upenn.cis650.structure.Node;

public class JsoupParser extends SourceParser {
	
	
	private Source source;
	
	/**
	 * Jsoup Document object
	 */
	private Document document;
	
	public JsoupParser(Source s) {
		try {
			createParser(s);
			this.source = s;
		} catch (InitializationErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public List<Node> find(Query query) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	/**
	 * For the JSoup Parser, this method only searches for text
	 * at the same level as that of the node, and not of the children nodes
	 */
	public List<Node> findContainingText(String text) {
		
		Elements matchingElements = document.getElementsContainingOwnText(text);
		
		ArrayList<Node> resultList = new ArrayList<Node>();
		Node n = null;
		for(Element matchedElement: matchingElements) {
			 n = new Node(matchedElement.text(),source);
			
			//Hack: Storing DOM Tree element object also as a feature of the node
			n.addFeature("Element", matchedElement);
			
			//Adding other relevant properties to the feature/property map
			n.addFeature("class", matchedElement.className());
			n.addFeature("id", matchedElement.id());
			n.addFeature("tag", matchedElement.tag());
			n.addFeature("index", matchedElement.siblingIndex());
			
			resultList.add(n);
			
		}
		
		return resultList;
	}
	
	
	/**
	 * Sets the document object 
	 * @throws InitializationErrorException 
	 * @throws IOException 
	 */
	@Override
	public void createParser(Source s) throws InitializationErrorException {
		
		//Check if the source is a URL
		if(s instanceof URLSource) {
			try {
				document = Jsoup.connect(s.getSourceAddresss()).get();
			} 
			catch (IOException e) {
				e.printStackTrace();
				throw new InitializationErrorException("Jsoup object cannot be created");
			}
		}
		else {
			document = new Document(s.getSourceContents());
		}
		
	}
	
	/**
	 * Utility method to find the XPath of the given Node
	 * @param node the Node containing the element
	 * @return the Query or null, if the node is not a true DOM Node/ object does not exist
	 */
	public Query generateXPath(Node node) {
		
		Element element = (Element) node.getFeature("Element");
		
		if(element == null) {
			return null;
		}
		
		StringBuilder xpathexp = new StringBuilder();
		
		while(element != null) {
			xpathexp.insert(0, "/" + element.tagName() + "[" + element.elementSiblingIndex() + "]");
			element = element.parent();
		}
		
		return new Query(xpathexp.toString());
		
	}
	
	
	/**
	 * Utility method to find the Jsoup Query of the given node
	 * @param node
	 * @return
	 */
	public Query generateJSoupQuery(Node node) {
		
		Element element = (Element) node.getFeature("Element");
		
		if(element == null) {
			return null;
		}
		
		StringBuilder jsoupexp = new StringBuilder();
		
		while(element != null) {
			jsoupexp.insert(0, " > " + element.tagName() + ":eq(" + element.elementSiblingIndex() + ")");
			element = element.parent();
		}
		
		return new Query(jsoupexp.toString());
		
	}
	

}
