package net.cassite.pure.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.annotations.Singleton;
import net.cassite.pure.ioc.handlers.*;
import net.cassite.pure.ioc.handlers.constructor.ConstructorDefaultFilter;
import net.cassite.pure.ioc.handlers.constructor.DefaultConstructorFilter;
import net.cassite.pure.ioc.handlers.param.DefaultParamHandler;
import net.cassite.pure.ioc.handlers.param.ParamForceHandler;
import net.cassite.pure.ioc.handlers.param.ParamUseHandler;
import net.cassite.pure.ioc.handlers.param.PrimitiveParameterHandler;
import net.cassite.pure.ioc.handlers.setter.DefaultSetterHandler;
import net.cassite.pure.ioc.handlers.setter.SetterForceHandler;
import net.cassite.pure.ioc.handlers.setter.SetterIgnoreHandler;
import net.cassite.pure.ioc.handlers.setter.SetterUseHandler;
import net.cassite.pure.ioc.handlers.type.DefaultTypeHandler;
import net.cassite.pure.ioc.handlers.type.TypeDefaultHandler;
import net.cassite.pure.ioc.handlers.type.TypeIsSingletonHandler;
import net.cassite.pure.ioc.handlers.type.TypeWireHandler;
import net.cassite.style.Style;
import net.cassite.style.reflect.*;

/**
 * The class is to control the process of handling annotations and constructing
 * objects. <br/>
 * Annotation handlers are divided into 4 kinds. <br/>
 * <ul>
 * <li>TypeAnnotationHandler : enabled when constructing objects</li>
 * <li>ConstructorFilter : enabled when selecting constructors</li>
 * <li>ParamAnnotationHandler : enabled when getting parameter values of a
 * constructor</li>
 * <li>SetterAnnotationHandler : enabled when invoking a setter. <br/>
 * (field, method, parameter annotation of a setter are considered as 'setter
 * annotation'.)</li>
 * </ul>
 * All handlers process in a simple logic: <br/>
 * enable handlers in a chain, low priority handler are earlier to be called,
 * <br/>
 * handlers are recommended to call higher priority handlers, check return <br/>
 * value, then decide whether to run its own handling process.
 * 
 * @author wkgcass
 * 
 * @see net.cassite.pure.ioc.handlers.TypeHandlerChain
 * @see net.cassite.pure.ioc.handlers.TypeAnnotationHandler
 * @see org.purifier.ioc.handlers.TypeDefaultHandler
 * 
 * @see net.cassite.pure.ioc.handlers.ConstructorFilterChain
 * @see net.cassite.pure.ioc.handlers.ConstructorFilter
 * 
 * @see net.cassite.pure.ioc.handlers.ParamHandlerChain
 * @see org.purifier.ioc.handlersParamAnnotationHandler
 * 
 * @see net.cassite.pure.ioc.handlers.SetterHandlerChain
 * @see net.cassite.pure.ioc.handlers.SetterAnnotationHandler
 *
 */
public abstract class IOCController extends Style {

        private static Logger logger = Logger.getLogger(IOCController.class);

        protected IOCController() {
        }

        // ===========================
        // ==========handlers=========
        // ===========================

        /**
         * param annotation handlers
         */
        private static List<ParamAnnotationHandler> paramAnnotationHandlers = new ArrayList<ParamAnnotationHandler>();
        /**
         * constructor filters
         */
        private static List<ConstructorFilter> constructorFilters = new ArrayList<ConstructorFilter>();
        /**
         * setter annotation handlers
         */
        private static List<SetterAnnotationHandler> setterAnnotationHandlers = new ArrayList<SetterAnnotationHandler>();
        /**
         * type annotation handler
         */
        private static List<TypeAnnotationHandler> typeAnnotationHandlers = new ArrayList<TypeAnnotationHandler>();

        // ============================
        // =========container==========
        // ============================

        /**
         * constants<br/>
         */
        private static Map<String, Object> constants = new ConcurrentHashMap<String, Object>();
        /**
         * singleton class instances<br/>
         * classes with Singleton annotation
         * 
         * @see Singleton
         */
        private static Map<Class<?>, Object> singletons = new ConcurrentHashMap<Class<?>, Object>();
        /**
         * variables<br/>
         * <b>static</b> methods or fields of a class, will be invoked/get when
         * needed.
         */
        private static Map<String, MemberSup<?>> variables = new ConcurrentHashMap<String, MemberSup<?>>();

        /**
         * get instance by class. <br/>
         * this method would check TypeHandlerChain to generate instance.
         * 
         * @param cls
         * @return
         */
        protected static Object get(Class<?> cls) {
                logger.debug("Invoking get(Class) to get instance of " + cls);
                TypeHandlerChain chain = new TypeHandlerChain(typeAnnotationHandlers, cls.getAnnotations());
                return chain.next().handle(cls, chain);
        }

        /**
         * invoke a setter<br/>
         * This method would check corresponding field(might not found, that's
         * unimportant) and method annotations, then go through
         * SetterHandlerChain.
         * 
         * @param target
         *                object to invoke
         * @param m
         *                setter
         */
        @SuppressWarnings("unchecked")
        protected static void invokeSetter(Object target, MethodSupport<?, ?> m) {
                logger.debug("Wiring object " + target + "'s method " + m);

                List<FieldSupport<?, Object>> fields = cls(target).allFields();

                Set<Annotation> annset = new HashSet<Annotation>();

                // get inferred field name
                String fieldName = m.name().substring(3);

                // try to get field and its annotations ( ignore field name case
                // )
                If($(fields).findOne(f -> f.name().equalsIgnoreCase(fieldName)), (found) -> {
                        for (Annotation ann : found.getMember().getAnnotations())
                                annset.add(ann);
                }).End();

                // try to get method annotations
                for (Annotation ann : m.getMember().getAnnotations())
                        annset.add(ann);
                // parameter value annotations
                for (Annotation ann : m.getMember().getParameterAnnotations()[0]) {
                        annset.add(ann);
                }

                logger.debug("With Annotations: " + annset);

                // handle
                SetterHandlerChain chain = new SetterHandlerChain(setterAnnotationHandlers, annset);
                chain.next().handle(target, (MethodSupport<Object, Object>) m, annset, chain);
        }

        /**
         * construct with given class and parameter values.
         * 
         * @param con
         *                constructor to call
         * @param parameterValues
         *                parameter values
         * @return new instance generated by the constructor and parameterValues
         */
        private Object construct(ConstructorSup<?> con, Object[] parameterValues) {
                return con.newInstance(parameterValues);
        }

        /**
         * construct with given class <br/>
         * this method gets constructor from constructor filter chain, and then
         * get parameters from ParamHandlerChain. <br/>
         * finally call {@link #construct(ConstructorSup, Object[])}
         * 
         * @param cls
         *                class to construct
         * @return instance of the class
         * @see ParamHandlerChain
         */
        protected Object constructObject(@SuppressWarnings("rawtypes") Class cls) {
                logger.debug("Invoking constructObject(Class) to get instance of type " + cls);
                Set<Annotation> set = new HashSet<Annotation>();
                for (Constructor<?> cons : cls.getConstructors()) {
                        for (Annotation ann : cons.getAnnotations()) {
                                set.add(ann);
                        }
                }
                logger.debug("--gathered annotatiosn are " + set);
                ConstructorFilterChain chain = new ConstructorFilterChain(constructorFilters, set);
                @SuppressWarnings("unchecked")
                ConstructorSup<?> con = chain.next().handle(cls(cls).constructors(), chain);

                logger.debug("--retrieved constructor is " + con);

                Object[] pv = new Object[con.argCount()];
                for (int i = 0; i < pv.length; ++i) {
                        ParamHandlerChain chain2 = new ParamHandlerChain(paramAnnotationHandlers, con.getMember().getParameterAnnotations()[i]);
                        pv[i] = chain2.next().handle(con, con.argTypes()[i], con.getMember().getParameterAnnotations()[i], chain2);
                        logger.debug("--parameter at index " + i + " is " + pv[i]);
                }
                return construct(con, pv);
        }

        /**
         * get instance with class. <br/>
         * this method would check whether it IsSingleton <br/>
         * then invoke constructObject(Class&lt;?&gt;) to get instance<br/>
         * <br/>
         * This method might call singletons.get directly if the singleton class
         * already in or finished construction <b>or</b>
         * {@link #constructObject(Class)}<br/>
         * <b>Note</b> that, this method will construct the given class, it will
         * not go through TypeHandlerChain, e.g. it won't find out whether it's
         * redirected to another class(using Default annotation)<br/>
         * Use {@link #get(Class)} to do the type check.
         * 
         * @param cls
         *                class to instantiate
         * @return instance of the class.
         * @see #constructObject(Class)
         * @see #get(Class)
         */
        @SuppressWarnings("unchecked")
        protected Object getObject(@SuppressWarnings("rawtypes") Class cls) {
                logger.debug("Invoking getObject(Class) to get instance of type " + cls);
                if (cls.isAnnotationPresent(Singleton.class)) {
                        logger.debug("--is singleton");
                        if (singletons.containsKey(cls))
                                return singletons.get(cls);
                        else
                                return constructObject(cls);
                } else
                        return constructObject(cls);
        }

        /**
         * This method invokes given method with inferred arguments<br/>
         * It will check the ParamHandlerChain, and retrieve arguments.
         * 
         * @param method
         *                method to invoke
         * @param target
         *                object of the method to invoke on
         * @return invocation result
         * @see ParamHandlerChain
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
        protected static Object invokeMethod(MethodSupport method, Object target) {
                logger.debug("Invoking method " + method + " of object " + target);

                Object[] pv = new Object[method.argCount()];
                for (int i = 0; i < pv.length; ++i) {
                        ParamHandlerChain chain2 = new ParamHandlerChain(paramAnnotationHandlers, method.getMember().getParameterAnnotations()[i]);
                        pv[i] = chain2.next().handle(method, method.argTypes()[i], method.getMember().getParameterAnnotations()[i], chain2);
                }
                return method.invoke(target, pv);
        }

        // ================================
        // ========register/retrieve=======
        // ================================

        public static void register(ConstructorFilter ah) {
                logger.debug("registering " + ah);
                constructorFilters.add(ah);
        }

        public static void register(ParamAnnotationHandler ah) {
                logger.debug("registering " + ah);
                paramAnnotationHandlers.add(ah);
        }

        public static void register(SetterAnnotationHandler ah) {
                logger.debug("registering " + ah);
                setterAnnotationHandlers.add(ah);
        }

        public static void register(TypeAnnotationHandler ah) {
                logger.debug("registering " + ah);
                typeAnnotationHandlers.add(ah);
        }

        public static void registerConstant(String constName, Object constant) {
                constants.put(constName, constant);
        }

        static void registerSingleton(Object instance) {
                if (singletons.containsKey(instance.getClass()))
                        throw new ConstructingMultiSingletonException(instance.getClass());
                if (null != instance.getClass().getAnnotation(Singleton.class))
                        singletons.put(instance.getClass(), instance);
        }

        public static void registerVariable(String name, Member m) {
                if (m instanceof Method) {
                        variables.put(name, new MethodSupport<>((Method) m, Object.class, Object.class));
                } else if (m instanceof Field) {
                        variables.put(name, new FieldSupport<>((Field) m, Object.class, Object.class));
                }
        }

        @SuppressWarnings("unchecked")
        protected <T> T retrieveConstant(Class<T> type) {
                logger.debug("Retrieving Constant with type " + type);
                return (T) $(constants.values()).forEach(o -> {
                        return type.isInstance(o) ? BreakWithResult(o) : null;
                });
        }

        @SuppressWarnings("unchecked")
        public static <T> T retrieveConstant(String constName) {
                return (T) constants.get(constName);
        }

        @SuppressWarnings("unchecked")
        public static <T> T retrieveVariable(String variable) {
                MemberSup<?> m = variables.get(variable);
                if (null == m)
                        throw new IOCException(new NullPointerException(variable));
                if (m instanceof FieldSupport) {
                        FieldSupport<?, Object> f = (FieldSupport<?, Object>) m;
                        if (m.isStatic()) {
                                return (T) f.get(null);
                        } else {
                                return (T) f.get(get(f.getMember().getDeclaringClass()));
                        }
                }
                if (m instanceof MethodSupport) {
                        MethodSupport<?, Object> me = (MethodSupport<?, Object>) m;
                        if (m.isStatic()) {
                                return (T) invokeMethod(me, null);
                        } else {
                                return (T) invokeMethod(me, get(me.getMember().getDeclaringClass()));
                        }
                }
                return null;
        }

        /**
         * Automatically register built-in handlers
         */
        public static void autoRegister() {
                logger.debug("start auto registering...");
                register(new DefaultConstructorFilter());
                register(new ConstructorDefaultFilter());

                register(new PrimitiveParameterHandler());
                register(new DefaultParamHandler());
                register(new ParamUseHandler());
                register(new ParamForceHandler());

                register(new DefaultSetterHandler());
                register(new SetterUseHandler());
                register(new SetterForceHandler());
                register(new SetterIgnoreHandler());

                register(new TypeWireHandler());
                register(new DefaultTypeHandler());
                register(new TypeIsSingletonHandler());
                register(new TypeDefaultHandler());
        }

        public static void closeRegistering() {
                paramAnnotationHandlers = readOnly(paramAnnotationHandlers);
                setterAnnotationHandlers = readOnly(setterAnnotationHandlers);
                constructorFilters = readOnly(constructorFilters);
                typeAnnotationHandlers = readOnly(typeAnnotationHandlers);
                logger.debug("registration closed.");
        }
}
