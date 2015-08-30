package net.cassite.pure.ioc.handlers.type;

import java.lang.annotation.Annotation;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.annotations.Default;
import net.cassite.pure.ioc.handlers.TypeAnnotationHandler;
import net.cassite.pure.ioc.handlers.TypeHandlerChain;

/**
 * Priority 10.0 <br/>
 * Handler for Default annotation. <br/>
 * Instantiate the class that 'Default' annotation represents.
 * 
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.Default
 *
 */
public class TypeDefaultHandler extends IOCController implements TypeAnnotationHandler {

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
                try {
                        return chain.next().handle(cls, chain);
                } catch (AnnotationHandlingException e) {
                }

                Default ann = cls.getAnnotation(Default.class);

                Class<?> clazz = ann.clazz();

                return get(clazz);
        }

}
