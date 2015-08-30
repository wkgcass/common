package net.cassite.pure.ioc.handlers.type;

import java.lang.annotation.Annotation;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.handlers.TypeAnnotationHandler;
import net.cassite.pure.ioc.handlers.TypeHandlerChain;

/**
 * Default implementation of TypeAnnotationHandler <br/>
 * simply generate the object then return.
 * 
 * @author wkgcass
 *
 */
public class DefaultTypeHandler extends IOCController implements TypeAnnotationHandler {

        private static final Logger logger = Logger.getLogger(DefaultTypeHandler.class);

        @Override
        public boolean canHandle(Annotation[] annotations) {
                return true;
        }

        @Override
        public Object handle(Class<?> cls, TypeHandlerChain chain) throws AnnotationHandlingException {
                logger.debug("Entered DefaultTypeHandler with args: \n\tcls:\t" + cls + "\n\tchain:\t" + chain);
                try {
                        return chain.next().handle(cls, chain);
                } catch (AnnotationHandlingException e) {
                }

                logger.debug("start handling with DefaultTypeHandler");
                return constructObject(cls);
        }

}
