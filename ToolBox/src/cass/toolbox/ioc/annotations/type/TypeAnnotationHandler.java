package cass.toolbox.ioc.annotations.type;

import java.lang.annotation.Annotation;

import cass.toolbox.ioc.AnnotationHandlingException;

public interface TypeAnnotationHandler {
	boolean canHandle(Annotation[] annotations);

	Object handle(Class<?> cls, TypeHandlerChain chain) throws AnnotationHandlingException;
	
	double priority();
}
