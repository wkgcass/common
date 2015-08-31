package net.cassite.pure.ioc.handlers.param;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.handlers.IrrelevantAnnotationHandlingException;
import net.cassite.pure.ioc.handlers.ParamAnnotationHandler;
import net.cassite.pure.ioc.handlers.ParamHandlerChain;
import net.cassite.style.reflect.MemberSup;

/**
 * Default implementation of ParamAnnotationHandler <br>
 * simply return the corresponding value of given parameter type.
 * 
 * @author wkgcass
 *
 */
public class DefaultParamHandler extends IOCController implements ParamAnnotationHandler {

        private static final Logger LOGGER = Logger.getLogger(DefaultParamHandler.class);

        @Override
        public boolean canHandle(Annotation[] annotations) {
                return true;
        }

        @Override
        public Object handle(MemberSup<?> caller, Class<?> cls, Annotation[] toHandle, ParamHandlerChain chain) throws AnnotationHandlingException {
                LOGGER.debug("Entered DefaultParamHandler with args:\n\tcaller:\t" + caller + "\n\tcls:\t" + cls + "\n\ttoHandle:\t"
                                + Arrays.toString(toHandle) + "\n\tchain:\t" + chain);
                try {
                        return chain.next().handle(caller, cls, toHandle, chain);
                } catch (IrrelevantAnnotationHandlingException e) {
                        LOGGER.debug("Start handling with DefaultParamHandler");
                        return get(cls);
                }
        }

}
