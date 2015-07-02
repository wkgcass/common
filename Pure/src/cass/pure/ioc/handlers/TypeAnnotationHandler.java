package cass.pure.ioc.handlers;

import java.lang.annotation.Annotation;

import cass.pure.ioc.AnnotationHandlingException;

public interface TypeAnnotationHandler {
	boolean canHandle(Annotation[] annotations);

	Object handle(Class<?> cls, TypeHandlerChain chain) throws AnnotationHandlingException;
	
	double priority();
}
