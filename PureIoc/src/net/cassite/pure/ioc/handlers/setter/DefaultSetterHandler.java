package net.cassite.pure.ioc.handlers.setter;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.handlers.SetterAnnotationHandler;
import net.cassite.pure.ioc.handlers.SetterHandlerChain;
import net.cassite.style.reflect.MethodSupport;

/**
 * Default implementation of SetterAnnotationHandler <br>
 * simply invoke the method with object of method's parameter type.
 * 
 * @author wkgcass
 *
 */
public class DefaultSetterHandler extends IOCController implements SetterAnnotationHandler {

        private static final Logger LOGGER = Logger.getLogger(DefaultSetterHandler.class);

        @Override
        public boolean canHandle(Set<Annotation> annotations) {
                return true;
        }

        @Override
        public boolean handle(Object target, MethodSupport<Object, Object> setter, Set<Annotation> toHandle, SetterHandlerChain chain)
                        throws AnnotationHandlingException {
                LOGGER.debug("Entered DefaultSetterHandler with args: \n\ttarget:\t" + target + "\n\tsetter:\t" + setter + "\n\ttoHandle:\t"
                                + toHandle + "\n\tchain:\t" + chain);

                if (chain.next().handle(target, setter, toHandle, chain)) {
                        return true;
                }

                LOGGER.debug("Start handling with DefaultSetterHandler");

                Class<?> pt = setter.argTypes()[0];

                LOGGER.debug("--Trying to get instance of type " + pt);

                Object pv = null;
                if (pt.getName().startsWith("java.") || pt.getName().startsWith("javax.")) {
                        pv = get(pt);
                } else {
                        pv = retrieveConstant(pt);
                        if (null == pv) {
                                pv = get(pt);
                        }
                }

                LOGGER.debug("--Retrieved instance of type " + pt + " is " + pv + ", start invoking setter...");

                setter.invoke(target, pv);
                return true;
        }

}
