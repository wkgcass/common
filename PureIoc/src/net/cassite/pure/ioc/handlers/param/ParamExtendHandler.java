package net.cassite.pure.ioc.handlers.param;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.AutoWire;
import net.cassite.pure.ioc.ExtendingHandler;
import net.cassite.pure.ioc.Utils;
import net.cassite.pure.ioc.annotations.Extend;
import net.cassite.pure.ioc.handlers.IrrelevantAnnotationHandlingException;
import net.cassite.pure.ioc.handlers.ParamAnnotationHandler;
import net.cassite.pure.ioc.handlers.ParamHandlerChain;
import net.cassite.style.reflect.MemberSup;

/**
 * Handler for Extend annotation<br>
 * Simplifies the process of retreiving objects from other object factories
 * 
 * @author wkgcass
 *
 */
public class ParamExtendHandler implements ParamAnnotationHandler {

        private static final Logger LOGGER = Logger.getLogger(ParamExtendHandler.class);

        @Override
        public boolean canHandle(Annotation[] annotations) {
                return null != Utils.getAnno(Extend.class, annotations);
        }

        @Override
        public Object handle(MemberSup<?> caller, Class<?> cls, Annotation[] toHandle, ParamHandlerChain chain) throws AnnotationHandlingException {
                LOGGER.debug("Entered ParamExtendHandler with args:\n\tcaller:\t" + caller + "\n\tcls:\t" + cls + "\n\ttoHandle:\t"
                                + Arrays.toString(toHandle) + "\n\tchain:\t" + chain);
                try {
                        return chain.next().handle(caller, cls, toHandle, chain);
                } catch (IrrelevantAnnotationHandlingException e) {
                        LOGGER.debug("Start handling with ParamExtendHandler");

                        Extend extend = Utils.getAnno(Extend.class, toHandle);
                        @SuppressWarnings("unchecked")
                        ExtendingHandler handler = (ExtendingHandler) AutoWire.get(extend.handler());

                        LOGGER.debug("--retrieved extend handler is " + handler + ", filling in args " + Arrays.toString(extend.args()));

                        return handler.get(extend.args());
                }
        }

}
