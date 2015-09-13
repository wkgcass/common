package net.cassite.pure.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import static net.cassite.style.Style.*;
import net.cassite.style.reflect.MethodSupport;

/**
 * Some useful methods to use when creating annotation handlers.
 * 
 * @author wkgcass
 *
 */
public abstract class Utils {
        protected Utils() {
        }

        /**
         * Retrieve annotation of designated type from given annotations
         * 
         * @param annoCls
         *                class of the annotation
         * @param annos
         *                annotations to choose from
         * @return chosen annotation or null if not found.
         */
        @SuppressWarnings("unchecked")
        public static <A extends Annotation> A getAnno(Class<A> annoCls, Annotation[] annos) {
                for (Annotation anno : annos) {
                        if (anno.annotationType().equals(annoCls)) {
                                return (A) anno;
                        }
                }
                return null;
        }

        /**
         * Retrieve annotation of designated type from given annotations
         * 
         * @param annoCls
         *                class of the annotation
         * @param annos
         *                annotations to choose from
         * @return chosen annotation or null if not found.
         */
        @SuppressWarnings("unchecked")
        public static <A extends Annotation> A getAnno(Class<A> annoCls, Collection<Annotation> annos) {
                for (Annotation anno : annos) {
                        if (anno.annotationType().equals(annoCls)) {
                                return (A) anno;
                        }
                }
                return null;
        }

        /**
         * retrieve field with given name from given class
         * 
         * @param cls
         *                class to retrieve the field from
         * @param fieldName
         *                name of the field
         * @return retrieved field
         * 
         * @see Class#getDeclaredField(String)
         */
        public static Field getField(Class<?> cls, String fieldName) {
                try {
                        return cls.getDeclaredField(fieldName);
                } catch (Exception e) {
                        throw $(e);
                }
        }

        /**
         * Retrieve method with 0 parameter count and with given name from given
         * class
         * 
         * @param cls
         *                the class to retrieve from
         * @param name
         *                name of the method
         * @return retrieved method
         * 
         * @see Class#getDeclaredMethod(String, Class...)
         */
        public static Method getMethod(Class<?> cls, String name) {
                return getMethod(cls, name, new Class[0]);
        }

        /**
         * Retrieve method with given name and parameter types from given class
         * 
         * @param cls
         *                the class to retrieve from
         * @param name
         *                name of the method
         * @param parameterTypes
         *                parameter types
         * @return retrieved method
         * 
         * @see Class#getDeclaredMethod(String, Class...)
         */
        public static Method getMethod(Class<?> cls, String name, Class<?>... parameterTypes) {
                try {
                        return cls.getDeclaredMethod(name, parameterTypes);
                } catch (Exception e) {
                        throw $(e);
                }
        }

        /**
         * the following methods are invoked from IOCController's protected
         * methods, you can use them if you cann't extend IOCController
         */

        /**
         * @see IOCController#get(Class)
         */
        public static Object get(Class<?> cls) {
                return IOCController.get(cls);
        }

        /**
         * @see IOCController#invokeSetter(Object, MethodSupport)
         */
        public static void invokeSetter(Object target, MethodSupport<?, ?> m) {
                IOCController.invokeSetter(target, m);
        }

        /**
         * @see IOCController#constructObject(Class)
         */
        public Object constructObject(@SuppressWarnings("rawtypes") Class cls) {
                return IOCController.constructObject(cls);
        }

        /**
         * @see IOCController#getObject(Class)
         */
        public Object getObject(@SuppressWarnings("rawtypes") Class cls) {
                return IOCController.getObject(cls);
        }

        /**
         * @see IOCController#invokeMethod(MethodSupport, Object)
         */
        public static Object invokeMethod(@SuppressWarnings("rawtypes") MethodSupport method, Object target) {
                return IOCController.invokeMethod(method, target);
        }

        /**
         * @see IOCController#retrieveConstant(Class)
         */
        public static <T> T retrieveConstant(Class<T> type) {
                return IOCController.retrieveConstant(type);
        }
}
