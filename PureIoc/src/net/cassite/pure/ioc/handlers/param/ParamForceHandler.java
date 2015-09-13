package net.cassite.pure.ioc.handlers.param;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.annotations.Force;
import net.cassite.pure.ioc.handlers.IrrelevantAnnotationHandlingException;
import net.cassite.pure.ioc.handlers.ParamAnnotationHandler;
import net.cassite.pure.ioc.handlers.ParamHandlerChain;
import static net.cassite.style.aggregation.Aggregation.*;
import net.cassite.style.reflect.MemberSup;

/**
 * Handler for Force annotation. <br>
 * forces a value to be what the string represents.
 * 
 * @author wkgcass
 *
 * @see Force
 */
public class ParamForceHandler implements ParamAnnotationHandler {

        private static final Logger LOGGER = Logger.getLogger(ParamForceHandler.class);

        @Override
        public boolean canHandle(Annotation[] annotations) {
                for (Annotation ann : annotations) {
                        if (ann.annotationType() == Force.class) {
                                return true;
                        }
                }
                return false;
        }

        @Override
        public Object handle(MemberSup<?> caller, Class<?> cls, Annotation[] toHandle, ParamHandlerChain chain) throws AnnotationHandlingException {
                LOGGER.debug("Entered ParamForceHandler with args:\n\tcaller:\t" + caller + "\n\tcls:\t" + cls + "\n\ttoHandle:\t"
                                + Arrays.toString(toHandle) + "\n\tchain:\t" + chain);
                try {
                        return chain.next().handle(caller, cls, toHandle, chain);
                } catch (IrrelevantAnnotationHandlingException e) {
                        LOGGER.debug("Start handling with ParamForceHandler");

                        return If((Force) $(toHandle).findOne(a -> a.annotationType() == Force.class), f -> {
                                try {
                                        if (cls.isPrimitive()) {
                                                if (cls == int.class) {
                                                        return Integer.parseInt(f.value());
                                                } else if (cls == boolean.class) {
                                                        return Boolean.parseBoolean(f.value());
                                                } else if (cls == char.class) {
                                                        return f.value().charAt(0);
                                                } else if (cls == double.class) {
                                                        return Double.parseDouble(f.value());
                                                } else if (cls == float.class) {
                                                        return Float.parseFloat(f.value());
                                                } else if (cls == byte.class) {
                                                        return Byte.parseByte(f.value());
                                                } else if (cls == long.class) {
                                                        return Long.parseLong(f.value());
                                                } else if (cls == Short.class) {
                                                        return Short.parseShort(f.value());
                                                }
                                        } else if (cls == String.class) {
                                                return f.value();
                                        }
                                } catch (Exception e1) {
                                        throw new AnnotationHandlingException("parse failed", e1);
                                }
                                throw new AnnotationHandlingException("parse failed");
                        }).Else(() -> {
                                throw new IrrelevantAnnotationHandlingException();
                        });
                }
        }

}
