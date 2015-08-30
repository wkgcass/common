package net.cassite.pure.ioc.handlers.setter;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.annotations.Force;
import net.cassite.pure.ioc.handlers.SetterAnnotationHandler;
import net.cassite.pure.ioc.handlers.SetterHandlerChain;
import net.cassite.style.reflect.MethodSupport;

/**
 * Priority 8.0 <br/>
 * Handler for Force annotation <br/>
 * invoke the setter with what force.value represents.
 * 
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.Force
 * @see cass.toolbox.util.Utility_#stringToObject(Object)
 *
 */
public class SetterForceHandler extends IOCController implements SetterAnnotationHandler {

        private static final Logger logger = Logger.getLogger(SetterForceHandler.class);

        @Override
        public boolean canHandle(Set<Annotation> annotations) {
                for (Annotation ann : annotations) {
                        if (ann.annotationType() == Force.class) {
                                return true;
                        }
                }
                return false;
        }

        @Override
        public boolean handle(Object target, MethodSupport<Object, Object> setter, Set<Annotation> toHandle, SetterHandlerChain chain)
                        throws AnnotationHandlingException {
                logger.debug("Entered SetterForceHandler with args: \n\ttarget:\t" + target + "\n\tsetter:\t" + setter + "\n\ttoHandle:\t" + toHandle
                                + "\n\tchain:\t" + chain);

                if (chain.next().handle(target, setter, toHandle, chain)) {
                        return true;
                }

                logger.debug("Start handling with SetterForceHandler");

                Class<?> cls = setter.argTypes()[0];

                Object[] pv = new Object[] { If((Force) $(toHandle).findOne(a -> a.annotationType() == Force.class), f -> {
                        try {
                                if (cls.isPrimitive()) {
                                        if (cls == int.class || cls == Integer.class) {
                                                return Integer.parseInt(f.value());
                                        } else if (cls == boolean.class || cls == Boolean.class) {
                                                return Boolean.parseBoolean(f.value());
                                        } else if (cls == char.class || cls == Character.class) {
                                                return f.value().charAt(0);
                                        } else if (cls == double.class || cls == Double.class) {
                                                return Double.parseDouble(f.value());
                                        } else if (cls == float.class || cls == Float.class) {
                                                return Float.parseFloat(f.value());
                                        } else if (cls == byte.class || cls == Byte.class) {
                                                return Byte.parseByte(f.value());
                                        } else if (cls == long.class || cls == Long.class) {
                                                return Long.parseLong(f.value());
                                        } else if (cls == Short.class || cls == short.class) {
                                                return Short.parseShort(f.value());
                                        }
                                } else if (cls == String.class)
                                        return f.value();
                        } catch (Exception unimportant) {
                        }
                        throw new AnnotationHandlingException("parse failed");
                }).Else(() -> null) };

                logger.debug("--Inferred value is " + pv[0]);

                setter.invoke(target, pv);
                return true;
        }

}
