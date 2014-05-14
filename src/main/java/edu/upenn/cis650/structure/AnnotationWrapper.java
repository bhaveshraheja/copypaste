package edu.upenn.cis650.structure;

import gate.Annotation;

/*
 * A wrapper class for holding an annotation and a 
 * int(score) value associated with it
 */
public class AnnotationWrapper {

	private Annotation annotaiton;
	private int idDistance;
	
	public AnnotationWrapper(Annotation annotation, int idDistance) {
		this.annotaiton = annotation;
		this.idDistance = idDistance;
	}
	
	public Annotation getAnnotation() {
		return this.annotaiton;
	}
	
	public int getIdDistance() {
		return idDistance;
	}


}
