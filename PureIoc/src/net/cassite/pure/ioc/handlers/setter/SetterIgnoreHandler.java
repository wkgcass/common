package net.cassite.pure.ioc.handlers.setter;

import java.lang.annotation.Annotation;
import java.util.Set;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.annotations.Ignore;
import net.cassite.pure.ioc.handlers.SetterAnnotationHandler;
import net.cassite.pure.ioc.handlers.SetterHandlerChain;
import net.cassite.style.reflect.MethodSupport;

/**
 * Priority 10.0 <br/>
 * Handler for Ignore annotation. <br/>
 * the setter would be ignored.
 * 
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.Ignore
 *
 */
public class SetterIgnoreHandler implements SetterAnnotationHandler {

        @Override
        public boolean canHandle(Set<Annotation> annotations) {
                for (Annotation ann : annotations) {
                        if (ann.annotationType() == Ignore.class) {
                                return true;
                        }
                }
                return false;
        }

        @Override
        public boolean handle(Object target, MethodSupport<Object, Object> setter, Set<Annotation> toHandle, SetterHandlerChain chain)
                        throws AnnotationHandlingException {
                chain.next().handle(target, setter, toHandle, chain);
                return true;

        }

}
