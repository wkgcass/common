package net.cassite.pure.ioc.handlers.type;

import java.lang.annotation.Annotation;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.annotations.Singleton;
import net.cassite.pure.ioc.handlers.TypeAnnotationHandler;
import net.cassite.pure.ioc.handlers.TypeHandlerChain;

/**
 * Handler for IsSingleton annotation. <br>
 * the class would be considered as a singleton.
 * 
 * @author wkgcass
 * 
 * @see net.cassite.pure.ioc.annotations.Singleton
 *
 */
public class TypeIsSingletonHandler extends IOCController implements TypeAnnotationHandler {

        private static final Logger LOGGER = Logger.getLogger(TypeIsSingletonHandler.class);

        @Override
        public boolean canHandle(Annotation[] annotations) {
                for (Annotation ann : annotations) {
                        if (ann.annotationType() == Singleton.class) {
                                return true;
                        }
                }
                return false;
        }

        @Override
        public Object handle(Class<?> cls, TypeHandlerChain chain) throws AnnotationHandlingException {
                LOGGER.debug("Entered TypeIsSingletonHandler with args: \n\tcls:\t" + cls + "\n\tchain:\t" + chain);
                try {
                        return chain.next().handle(cls, chain);
                } catch (AnnotationHandlingException e) {
                        LOGGER.debug("start handling with TypeIsSingletonHandler");
                        return getObject(cls);
                }
        }

}
