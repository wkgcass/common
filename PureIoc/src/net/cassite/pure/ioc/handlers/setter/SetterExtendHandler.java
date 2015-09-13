package net.cassite.pure.ioc.handlers.setter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.AutoWire;
import net.cassite.pure.ioc.ExtendingHandler;
import net.cassite.pure.ioc.Utils;
import net.cassite.pure.ioc.annotations.Extend;
import net.cassite.pure.ioc.handlers.SetterAnnotationHandler;
import net.cassite.pure.ioc.handlers.SetterHandlerChain;
import net.cassite.style.reflect.MethodSupport;

/**
 * Handler for Extend annotation<br>
 * Simplifies the process of retreiving objects from other object factories
 * 
 * @author wkgcass
 *
 */
public class SetterExtendHandler implements SetterAnnotationHandler {

        private static final Logger LOGGER = Logger.getLogger(SetterExtendHandler.class);

        @Override
        public boolean canHandle(Set<Annotation> annotations) {
                return null != Utils.getAnno(Extend.class, annotations);
        }

        @Override
        public boolean handle(Object target, MethodSupport<Object, Object> setter, Set<Annotation> toHandle, SetterHandlerChain chain)
                        throws AnnotationHandlingException {
                LOGGER.debug("Entered SetterExtendHandler with args: \n\ttarget:\t" + target + "\n\tsetter:\t" + setter + "\n\ttoHandle:\t" + toHandle
                                + "\n\tchain:\t" + chain);
                if (chain.next().handle(target, setter, toHandle, chain)) {
                        return true;
                }

                LOGGER.debug("Start handling with SetterExtendHandler");

                Extend extend = Utils.getAnno(Extend.class, toHandle);
                @SuppressWarnings("unchecked")
                ExtendingHandler handler = (ExtendingHandler) AutoWire.get(extend.handler());

                LOGGER.debug("--retrieved extend handler is " + handler + ", filling in args " + Arrays.toString(extend.args()));

                setter.invoke(target, handler.get(extend.args()));

                return true;
        }

}
