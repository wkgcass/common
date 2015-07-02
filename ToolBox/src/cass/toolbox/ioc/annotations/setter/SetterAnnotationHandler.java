package cass.toolbox.ioc.annotations.setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import cass.toolbox.ioc.AnnotationHandlingException;

public interface SetterAnnotationHandler {
	boolean canHandle(Set<Annotation> annotations);

	boolean handle(Object target, Method setter, Set<Annotation> toHandle, SetterHandlerChain chain) throws AnnotationHandlingException;
	
	double priority();
}
