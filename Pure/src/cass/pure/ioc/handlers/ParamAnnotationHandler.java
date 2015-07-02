package cass.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

import cass.pure.ioc.AnnotationHandlingException;

public interface ParamAnnotationHandler {
	boolean canHandle(Annotation[] annotations);

	Object handle(Member caller, Class<?> cls,Annotation[] toHandle, ParamHandlerChain chain) throws AnnotationHandlingException;
	
	double priority();
}
