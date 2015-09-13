package net.cassite.pure.ioc.handlers.param;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.annotations.Use;
import net.cassite.pure.ioc.handlers.IrrelevantAnnotationHandlingException;
import net.cassite.pure.ioc.handlers.ParamAnnotationHandler;
import net.cassite.pure.ioc.handlers.ParamHandlerChain;
import net.cassite.style.reflect.MemberSup;

import static net.cassite.style.Style.*;

/**
 * Handler for Use annotation. <br>
 * returns the instance of the class "use" annotation represents.
 * 
 * @author wkgcass
 * 
 * @see Use
 *
 */
public class ParamUseHandler extends IOCController implements ParamAnnotationHandler {

        private static final Logger LOGGER = Logger.getLogger(ParamUseHandler.class);

        @Override
        public boolean canHandle(Annotation[] annotations) {
                for (Annotation ann : annotations) {
                        if (ann.annotationType() == Use.class) {
                                return true;
                        }
                }
                return false;
        }

        @Override
        public Object handle(MemberSup<?> caller, Class<?> cls, Annotation[] toHandle, ParamHandlerChain chain) throws AnnotationHandlingException {
                LOGGER.debug("Entered ParamUseHandler with args:\n\tcaller:\t" + caller + "\n\tcls:\t" + cls + "\n\ttoHandle:\t"
                                + Arrays.toString(toHandle) + "\n\tchain:\t" + chain);

                try {
                        return chain.next().handle(caller, cls, toHandle, chain);
                } catch (IrrelevantAnnotationHandlingException e) {
                        LOGGER.debug("Start handling with ParamUseHandler");

                        return If((Use) $(toHandle).findOne(a -> a.annotationType() == Use.class), use -> {
                                Class<?> clazz = use.clazz();
                                if (clazz != Use.class) {
                                        return get(clazz);
                                }
                                String constant = use.constant();
                                if (!"".equals(constant)) {
                                        return retrieveConstant(constant);
                                }
                                String variable = use.variable();
                                if (!"".equals(variable)) {
                                        return retrieveVariable(variable);
                                }
                                throw new AnnotationHandlingException("empty Use annotation");
                        }).Else(() -> {
                                throw new IrrelevantAnnotationHandlingException();
                        });
                }
        }

}
