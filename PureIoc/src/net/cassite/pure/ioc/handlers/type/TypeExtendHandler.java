package net.cassite.pure.ioc.handlers.type;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.AutoWire;
import net.cassite.pure.ioc.ExtendingHandler;
import net.cassite.pure.ioc.Utils;
import net.cassite.pure.ioc.annotations.Extend;
import net.cassite.pure.ioc.handlers.IrrelevantAnnotationHandlingException;
import net.cassite.pure.ioc.handlers.TypeAnnotationHandler;
import net.cassite.pure.ioc.handlers.TypeHandlerChain;

/**
 * Handler of Extend annotation
 * 
 * @author wkgcass
 *
 */
public class TypeExtendHandler implements TypeAnnotationHandler {

        private static final Logger LOGGER = Logger.getLogger(TypeExtendHandler.class);

        @Override
        public boolean canHandle(Annotation[] annotations) {
                return null != Utils.getAnno(Extend.class, annotations);
        }

        @Override
        public Object handle(Class<?> cls, TypeHandlerChain chain) throws AnnotationHandlingException {
                LOGGER.debug("Entered TypeExtendHandler with args: \n\tcls:\t" + cls + "\n\tchain:\t" + chain);

                try {
                        return chain.next().handle(cls, chain);
                } catch (IrrelevantAnnotationHandlingException e) {
                        LOGGER.debug("Start handling with TypeExtendHandler");

                        Extend extend = Utils.getAnno(Extend.class, cls.getAnnotations());
                        ExtendingHandler handler = AutoWire.get(extend.handler());

                        LOGGER.debug("--retrieved extend handler is " + handler + ", filling in args " + Arrays.toString(extend.args()));

                        return handler.get(extend.args());
                }
        }

}
