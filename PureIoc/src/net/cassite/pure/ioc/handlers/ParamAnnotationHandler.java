package net.cassite.pure.ioc.handlers;

import java.lang.annotation.Annotation;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.style.reflect.MemberSup;

public interface ParamAnnotationHandler {
	boolean canHandle(Annotation[] annotations);

	Object handle(MemberSup<?> caller, Class<?> cls,Annotation[] toHandle, ParamHandlerChain chain) throws AnnotationHandlingException;
}
