package net.cassite.pure.ioc.handlers.type;

import java.lang.annotation.Annotation;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.annotations.Default;
import net.cassite.pure.ioc.handlers.IrrelevantAnnotationHandlingException;
import net.cassite.pure.ioc.handlers.TypeAnnotationHandler;
import net.cassite.pure.ioc.handlers.TypeHandlerChain;

/**
 * Handler for Default annotation. <br>
 * Instantiate the class that 'Default' annotation represents.
 * 
 * @author wkgcass
 * 
 * @see net.cassite.pure.ioc.annotations.Default
 *
 */
public class TypeDefaultHandler extends IOCController implements TypeAnnotationHandler {

        private static final Logger LOGGER = Logger.getLogger(TypeDefaultHandler.class);

        @Override
        public boolean canHandle(Annotation[] annotations) {
                for (Annotation ann : annotations) {
                        if (ann.annotationType() == Default.class) {
                                return true;
                        }
                }
                return false;
        }

        @Override
        public Object handle(Class<?> cls, TypeHandlerChain chain) throws AnnotationHandlingException {
                LOGGER.debug("Entered TypeDefaultHandler with args: \n\tcls:\t" + cls + "\n\tchain:\t" + chain);
                try {
                        return chain.next().handle(cls, chain);
                } catch (IrrelevantAnnotationHandlingException e) {
                        LOGGER.debug("start handling with TypeDefaultHandler");

                        Default ann = cls.getAnnotation(Default.class);

                        Class<?> clazz = ann.clazz();

                        LOGGER.debug("--Redirecting to class " + clazz);
                        LOGGER.debug("Invoking get(Class) ...");
                        return get(clazz);
                }
        }

}
