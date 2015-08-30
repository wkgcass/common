package net.cassite.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.util.Set;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.style.reflect.MethodSupport;

public interface SetterAnnotationHandler {
        boolean canHandle(Set<Annotation> annotations);

        boolean handle(Object target, MethodSupport<Object, Object> setter, Set<Annotation> toHandle, SetterHandlerChain chain)
                        throws AnnotationHandlingException;
}
