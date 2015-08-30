package net.cassite.pure.ioc.handlers.param;

import java.lang.annotation.Annotation;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.handlers.ParamAnnotationHandler;
import net.cassite.pure.ioc.handlers.ParamHandlerChain;
import net.cassite.style.reflect.MemberSup;

/**
 * Default implementation of ParamAnnotationHandler <br/>
 * simply return the corresponding value of given parameter type.
 * 
 * @author wkgcass
 *
 */
public class DefaultParamHandler extends IOCController implements ParamAnnotationHandler {

        @Override
        public boolean canHandle(Annotation[] annotations) {
                return true;
        }

        @Override
        public Object handle(MemberSup<?> caller, Class<?> cls, Annotation[] toHandle, ParamHandlerChain chain) throws AnnotationHandlingException {
                try {
                        return chain.next().handle(caller, cls, toHandle, chain);
                } catch (AnnotationHandlingException e) {
                }
                return get(cls);
        }

}
