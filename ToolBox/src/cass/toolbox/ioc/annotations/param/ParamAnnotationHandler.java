package cass.toolbox.ioc.annotations.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

import cass.toolbox.ioc.AnnotationHandlingException;

public interface ParamAnnotationHandler {
	boolean canHandle(Annotation[] annotations);

	Object handle(Member caller, Class<?> cls,Annotation[] toHandle, ParamHandlerChain chain) throws AnnotationHandlingException;
	
	double priority();
}
