package net.cassite.pure.ioc.handlers;

import java.lang.annotation.Annotation;

import net.cassite.pure.ioc.AnnotationHandlingException;

public interface TypeAnnotationHandler {
	boolean canHandle(Annotation[] annotations);

	Object handle(Class<?> cls, TypeHandlerChain chain) throws AnnotationHandlingException;
}
