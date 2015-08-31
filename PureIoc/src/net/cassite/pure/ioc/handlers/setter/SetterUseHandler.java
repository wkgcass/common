package net.cassite.pure.ioc.handlers.setter;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.annotations.Use;
import net.cassite.pure.ioc.handlers.SetterAnnotationHandler;
import net.cassite.pure.ioc.handlers.SetterHandlerChain;
import net.cassite.style.reflect.MethodSupport;

/**
 * <br>
 * Handler for Use annotation. <br>
 * Use object of the type that the annotation designated.
 * 
 * @author wkgcass
 * 
 * @see Use
 *
 */
public class SetterUseHandler extends IOCController implements SetterAnnotationHandler {

        private static final Logger LOGGER = Logger.getLogger(SetterUseHandler.class);

        @Override
        public boolean canHandle(Set<Annotation> annotations) {
                for (Annotation ann : annotations) {
                        if (ann.annotationType() == Use.class) {
                                return true;
                        }
                }
                return false;
        }

        @Override
        public boolean handle(Object target, MethodSupport<Object, Object> setter, Set<Annotation> toHandle, SetterHandlerChain chain)
                        throws AnnotationHandlingException {
                LOGGER.debug("Entered SetterUseHandler with args: \n\ttarget:\t" + target + "\n\tsetter:\t" + setter + "\n\ttoHandle:\t" + toHandle
                                + "\n\tchain:\t" + chain);

                if (chain.next().handle(target, setter, toHandle, chain)) {
                        return true;
                }

                LOGGER.debug("Start handling with SetterUseHandler");

                Use use = null;
                for (Annotation ann : toHandle) {
                        if (ann.annotationType() == Use.class) {
                                use = (Use) ann;
                        }
                }

                Object obj = null;

                Class<?> clazz = use.clazz();
                if (clazz != Use.class) {
                        obj = get(clazz);
                }

                String constant = use.constant();
                if (!"".equals(constant)) {
                        obj = retrieveConstant(constant);
                }

                String variable = use.variable();
                if (!"".equals(variable)) {
                        obj = retrieveVariable(variable);
                }

                LOGGER.debug("--Inferred parameter value is " + obj);

                Object[] pv = new Object[] { obj };
                setter.invoke(target, pv);

                return true;
        }

}
