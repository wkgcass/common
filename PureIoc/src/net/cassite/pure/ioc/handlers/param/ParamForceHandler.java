package net.cassite.pure.ioc.handlers.param;

import java.lang.annotation.Annotation;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.annotations.Force;
import net.cassite.pure.ioc.handlers.IrrelevantAnnotationHandlingException;
import net.cassite.pure.ioc.handlers.ParamAnnotationHandler;
import net.cassite.pure.ioc.handlers.ParamHandlerChain;
import net.cassite.style.aggregation.Aggregation;
import net.cassite.style.reflect.MemberSup;

/**
 * Handler for Force annotation. <br/>
 * forces a value to be what the string represents. <br/>
 * e.g. "null","nulptr","" represents null, numbers represents integer, <br/>
 * number with dot in it represents double.
 * 
 * @author wkgcass
 *
 * @see cass.toolbox.ioc.annotations.Force
 * @see cass.toolbox.util.Utility_#stringToObject(Object)
 */
public class ParamForceHandler extends Aggregation implements ParamAnnotationHandler {

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
                try {
                        return chain.next().handle(caller, cls, toHandle, chain);
                } catch (AnnotationHandlingException e) {
                }
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
                                }
                        } catch (Exception unimportant) {
                        }
                        throw new AnnotationHandlingException("parse failed");
                }).Else(() -> {
                        throw new IrrelevantAnnotationHandlingException();
                });
        }

}
