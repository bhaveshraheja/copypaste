package edu.upenn.cis650.parsers;

import java.util.HashMap;
import java.util.Map;

import edu.upenn.cis650.sources.Source;

public class ParserManager {

	private static Map<Source, SourceParser> parserPool= new HashMap<Source, SourceParser>();
	
	
	/**
	 * Searches the pool of existing parsers for the parser
	 * for this source. 
	 * @param s
	 * @return
	 */
	public static SourceParser getSourceParser(Source s) {
		SourceParser x;
		
		x = parserPool.get(s);
		if(x == null) {
			x = new JsoupParser(s);
			/*For now, by default this resolves to a 
			 * Jsoup Parser. Future implementations 
			 * can choose to add different kind of parsers*/
			parserPool.put(s, x);
		}
		System.out.println("Source received"+s.getSourceAddresss()+"|Parser sent"+x.toString());
		return x;
	}
	
	
	

}
