package net.cassite.pure.ioc.handlers.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.handlers.TypeAnnotationHandler;
import net.cassite.pure.ioc.handlers.TypeHandlerChain;

/**
 * Default implementation of TypeAnnotationHandler <br/>
 * simply generate the object then return.
 * 
 * @author wkgcass
 *
 */
public class DefaultTypeHandler extends IOCController implements TypeAnnotationHandler {

        private static final Logger logger = Logger.getLogger(DefaultTypeHandler.class);

        @Override
        public boolean canHandle(Annotation[] annotations) {
                return true;
        }

        @Override
        public Object handle(Class<?> cls, TypeHandlerChain chain) throws AnnotationHandlingException {
                logger.debug("Entered DefaultTypeHandler with args: \n\tcls:\t" + cls + "\n\tchain:\t" + chain);
                try {
                        return chain.next().handle(cls, chain);
                } catch (AnnotationHandlingException e) {
                }

                logger.debug("start handling with DefaultTypeHandler");
                if (cls.isPrimitive()) {
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
                }

                return constructObject(cls);
        }

}
