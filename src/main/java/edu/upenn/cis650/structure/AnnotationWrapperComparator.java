package edu.upenn.cis650.structure;

import java.util.Comparator;

import gate.Annotation;

/**
 * Comparator to sort the Annotation Wrapper based on smaller id distance
 * @author bhaveshraheja
 *
 */
public class AnnotationWrapperComparator implements Comparator<AnnotationWrapper> {


	
	public int compare(AnnotationWrapper o1, AnnotationWrapper o2) {
		// TODO Auto-generated method stub
		return (o1.getIdDistance() <= o2.getIdDistance()) ? 1 : -1; 
	}

	


}
