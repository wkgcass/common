package net.cassite.pure.ioc.handlers.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.handlers.IrrelevantAnnotationHandlingException;
import net.cassite.pure.ioc.handlers.ParamAnnotationHandler;
import net.cassite.pure.ioc.handlers.ParamHandlerChain;
import net.cassite.style.reflect.MemberSup;

/**
 * Handles primitives and arrays.
 * 
 * @author wkgcass
 *
 */
public class PrimitiveParameterHandler implements ParamAnnotationHandler {

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

                if (cls.isPrimitive()) {
                        // primitive
                        if (cls == boolean.class) {
                                return (new Boolean(false));
                        }
                        if (cls == int.class) {
                                return (new Integer(0));
                        }
                        if (cls == short.class) {
                                return (new Short((short) 0));
                        }
                        if (cls == long.class) {
                                return (new Long(0));
                        }
                        if (cls == byte.class) {
                                return (new Byte((byte) 0));
                        }
                        if (cls == double.class) {
                                return (new Double(0));
                        }
                        if (cls == float.class) {
                                return (new Float(0));
                        }
                        return (new Character((char) 0));
                } else if (cls.isArray()) {
                        if (cls.getComponentType().isPrimitive()) {
                                // not primitive & is array & component is
                                // primitive
                                Class<?> clscmp = cls.getComponentType();
                                if (clscmp == boolean.class) {
                                        return (new boolean[0]);
                                }
                                if (clscmp == int.class) {
                                        return (new int[0]);
                                }
                                if (clscmp == short.class) {
                                        return (new short[0]);
                                }
                                if (clscmp == long.class) {
                                        return (new long[0]);
                                }
                                if (clscmp == byte.class) {
                                        return (new byte[0]);
                                }
                                if (clscmp == double.class) {
                                        return (new double[0]);
                                }
                                if (clscmp == float.class) {
                                        return (new float[0]);
                                }
                                return (new char[0]);
                        }
                        // not primitive & is array & component is not primitive
                        return Array.newInstance(cls.getComponentType(), 0);
                } else {
                        throw new IrrelevantAnnotationHandlingException();
                }
        }

}
