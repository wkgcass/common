package cass.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Set;

import cass.pure.ioc.AnnotationHandlingException;

public interface ConstructorFilter {
	boolean canHandle(Set<Annotation> annotations);

	Constructor<?> handle(Constructor<?>[] cons, ConstructorFilterChain chain) throws AnnotationHandlingException;
	
	double priority();
}
