package net.cassite.pure.ioc.handlers.setter;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.annotations.Ignore;
import net.cassite.pure.ioc.handlers.SetterAnnotationHandler;
import net.cassite.pure.ioc.handlers.SetterHandlerChain;
import net.cassite.style.reflect.MethodSupport;

/**
 * Handler for Ignore annotation. <br>
 * the setter would be ignored.
 * 
 * @author wkgcass
 * 
 * @see Ignore
 *
 */
public class SetterIgnoreHandler implements SetterAnnotationHandler {

        private static final Logger LOGGER = Logger.getLogger(DefaultSetterHandler.class);

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
                LOGGER.debug("Entered SetterIgnoreHandler with args: \n\ttarget:\t" + target + "\n\tsetter:\t" + setter + "\n\ttoHandle:\t" + toHandle
                                + "\n\tchain:\t" + chain);
                if (!chain.next().handle(target, setter, toHandle, chain)) {
                        LOGGER.debug("Start handling with SetterIgnoreHandler");
                }
                return true;

        }

}
