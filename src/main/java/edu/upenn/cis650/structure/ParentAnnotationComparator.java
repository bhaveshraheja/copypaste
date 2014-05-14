package edu.upenn.cis650.structure;

import gate.Annotation;

import java.util.Comparator;

/**
 * May be incompatible with equals
 * @author bhaveshraheja
 *
 */
public class ParentAnnotationComparator implements Comparator<Annotation> {

	
	public int compare(Annotation arg0, Annotation arg1) {
		
		
		//if arg0 < arg1, return negative
		
		//Return the greater of the start offset nodes
		long sf1 = arg0.getStartNode().getOffset();
		long sf2 = arg1.getStartNode().getOffset();
		
		long ef1 = arg0.getEndNode().getOffset();
		long ef2 = arg1.getEndNode().getOffset();
		
		/*
		 * Look at the following (in order)
		 * 1. Greater Start Node offset
		 * 2. Lesser End Node offset
		 * 3. Greater annotation id
		 */
		
		
		return((sf1 > sf2) ? 1 : (sf1 < sf2) ? -1 : (ef1 < ef2) ? 1 : (ef1 > ef2) ?  -1 : (arg0.getId() > arg1.getId()) ? 1 : (arg0.getId() < arg1.getId()) ? -1 : 0);
		
	}

}
