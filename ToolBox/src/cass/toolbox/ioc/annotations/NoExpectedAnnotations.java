package cass.toolbox.ioc.annotations;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import cass.toolbox.ioc.AnnotationHandlingException;

public class NoExpectedAnnotations extends AnnotationHandlingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3683055063243362778L;
	public NoExpectedAnnotations(Annotation[] containing,Class<?>... anns){
		super("Contains"+Arrays.toString(containing)+", expecting types"+Arrays.toString(anns));
	}
}
