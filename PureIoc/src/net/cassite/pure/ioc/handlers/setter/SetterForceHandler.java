package net.cassite.pure.ioc.handlers.setter;

import java.lang.annotation.Annotation;
import java.util.Set;

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
                if (chain.next().handle(target, setter, toHandle, chain)) {
                        return true;
                }

                Class<?> cls = setter.argTypes()[0];

                Object[] pv = new Object[] { If((Force) $(toHandle).findOne(a -> a.annotationType() == Force.class), f -> {
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
                                }
                        } catch (Exception unimportant) {
                        }
                        throw new AnnotationHandlingException("parse failed");
                }).Else(() -> null) };

                setter.invoke(target, pv);
                return true;
        }

}
