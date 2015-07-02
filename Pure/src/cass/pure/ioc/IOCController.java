package cass.pure.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cass.pure.ioc.annotations.Singleton;
import cass.pure.ioc.handlers.ConstructorFilter;
import cass.pure.ioc.handlers.ConstructorFilterChain;
import cass.pure.ioc.handlers.ParamAnnotationHandler;
import cass.pure.ioc.handlers.ParamHandlerChain;
import cass.pure.ioc.handlers.SetterAnnotationHandler;
import cass.pure.ioc.handlers.SetterHandlerChain;
import cass.pure.ioc.handlers.TypeAnnotationHandler;
import cass.pure.ioc.handlers.TypeHandlerChain;
import cass.pure.ioc.listener.IOCListener;

/**
 * The class is to control the process of handling annotations and constructing objects.
 * <br/>Annotation handlers are divided into 4 kinds.
 * <br/><ul>
 * 				<li>TypeAnnotationHandler : enabled when constructing objects</li>
 * 				<li>ConstructorFilter : enabled when selecting constructors</li>
 * 				<li>ParamAnnotationHandler : enabled when getting parameter values of a constructor</li>
 * 				<li>SetterAnnotationHandler : enabled when invoking a setter.
 * 				<br/>(field, method, parameter annotation of a setter are considered as 'setter annotation'.)</li>
 * </ul>
 * All handlers process in a simple logic:
 * <br/>enable handlers in a chain, low priority handler are earlier to be called,
 * <br/>handlers are recommended to call higher priority handlers, check return
 * <br/>value, then decide whether to run its own handling process.
 * @author wkgcass
 * 
 * @see cass.pure.ioc.handlers.TypeHandlerChain
 * @see cass.pure.ioc.handlers.TypeAnnotationHandler
 * @see org.purifier.ioc.handlers.TypeDefaultHandler
 * 
 * @see cass.pure.ioc.handlers.ConstructorFilterChain
 * @see cass.pure.ioc.handlers.ConstructorFilter
 * 
 * @see cass.pure.ioc.handlers.ParamHandlerChain
 * @see org.purifier.ioc.handlersParamAnnotationHandler
 * 
 * @see cass.pure.ioc.handlers.SetterHandlerChain
 * @see cass.pure.ioc.handlers.SetterAnnotationHandler
 *
 */
public abstract class IOCController {
	/**
	 * param annotation handlers
	 */
	private static Set<ParamAnnotationHandler> paramAnnotationHandlers = new HashSet<ParamAnnotationHandler>();
	/**
	 * constructor filters
	 */
	private static Set<ConstructorFilter> constructorFilters = new HashSet<ConstructorFilter>();
	/**
	 * constants
	 */
	private static Map<String, Object> constants = new HashMap<String, Object>();
	/**
	 * setter annotation handlers
	 */
	private static Set<SetterAnnotationHandler> setterAnnotationHandlers = new HashSet<SetterAnnotationHandler>();
	/**
	 * listeners for 8 main methods
	 */
	private static Set<IOCListener> listeners=new HashSet<IOCListener>();
	/**
	 * singleton class instances
	 */
	private static Map<Class<?>, Object> singletons = new HashMap<Class<?>, Object>();
	/**
	 * type annotation handler
	 */
	private static Set<TypeAnnotationHandler> typeAnnotationHandlers = new HashSet<TypeAnnotationHandler>();
	/**
	 * variables
	 */
	private static Map<String, Member> variables=new HashMap<String, Member>();

	/**
	 * get instance by class.
	 * <br/>this method would check type handler chain to generate instance.
	 * <br/>beans are not recommended to retrieve with this method, use getBean(String) instead.
	 * @param cls
	 * @return
	 */
	protected static Object get(Class<?> cls) {
		for(IOCListener l : listeners){
			l.listenGet(cls);
		}
		TypeHandlerChain chain = new TypeHandlerChain(typeAnnotationHandlers, cls
				.getAnnotations());
		return chain.next().handle(cls, chain);
	}

	/**
	 * invoke a setter
	 * @param target object to invoke
	 * @param m setter
	 */
	protected static void invokeSetter(Object target, Method m) {
		for(IOCListener l : listeners){
			l.listenInvokeSetter(target, m);
		}
		
		Field[] fields = target.getClass().getDeclaredFields();

		Set<Annotation> annset = new HashSet<Annotation>();

		// get inferred field name
		String fieldName = m.getName().substring(3);

		// try to get field and its annotations ( ignore field name case )
		Field found = null;
		for (Field f : fields) {
			if (f.getName().equalsIgnoreCase(fieldName)) {
				found = f;
				break;
			}
		}
		if (null != found) {
			for (Annotation ann : found.getAnnotations()) {
				annset.add(ann);
			}
		}

		// try to get method annotations
		for (Annotation ann : m.getAnnotations()) {
			annset.add(ann);
		}

		// try to get parameter annotations
		Annotation[][] pvanns = m.getParameterAnnotations();
		for (Annotation ann : pvanns[0]) {
			annset.add(ann);
		}

		// handle
		SetterHandlerChain chain = new SetterHandlerChain(setterAnnotationHandlers, annset);
		chain.next().handle(target, m, annset, chain);
	}

	public static void register(ConstructorFilter ah) {
		constructorFilters.add(ah);
	}

	public static void register(IOCListener listener){
		listeners.add(listener);
	}

	public static void register(ParamAnnotationHandler ah) {
		paramAnnotationHandlers.add(ah);
	}
	
	public static void register(SetterAnnotationHandler ah) {
		setterAnnotationHandlers.add(ah);
	}
	
	public static void register(TypeAnnotationHandler ah) {
		typeAnnotationHandlers.add(ah);
	}
	
	public static void registerConstant(String constName, Object constant){
		constants.put(constName, constant);
	}
	
	static void registerSingleton(Object instance){
		synchronized(singletons){
			if(singletons.containsKey(instance.getClass())) throw new ConstructingMultiSingletonException(instance.getClass());
			if(null != instance.getClass().getAnnotation(Singleton.class)){
				singletons.put(instance.getClass(), instance);
			}
		}
	}

	public static void registerVariable(String name, Member m){
		if(m instanceof Method || m instanceof Field){
			variables.put(name, m);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T retrieveConstant(String constName){
		return (T)constants.get(constName);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T retrieveVariable(String variable){
		Member m=variables.get(variable);
		if(null==m) throw new IOCException(new NullPointerException(variable));
		boolean isStatic = Modifier.isStatic(m.getModifiers());
		try{
			if(m instanceof Field){
				Field f=(Field)m;
				if(isStatic){
					return (T)f.get(null);
				}else{
					return (T)f.get(get(f.getDeclaringClass()));
				}
			}
			if(m instanceof Method){
				Method me=(Method)m;
				if(isStatic){
					return (T) invokeMethod(me,null);
				}else{
					return (T) invokeMethod(me,get(me.getDeclaringClass()));
				}
			}
		}catch(Exception e){
			if(e instanceof InvocationTargetException){
				Throwable t=((InvocationTargetException)e).getTargetException();
				if(t instanceof Error) throw (Error)t;
				if(t instanceof RuntimeException) throw (RuntimeException)t;
			}
			if(e instanceof RuntimeException) throw (RuntimeException)e;
			throw new IOCException(e);
		}
		throw new IOCException();
	}

	protected IOCController() {
	}

	/**
	 * construct with given class and parameter values.
	 * 
	 * @param con constructor to call
	 * @param parameterValues parameter values
	 * @return new instance generated by the constructor and pv
	 */
	private Object construct(Constructor<?> con, Object[] parameterValues) {
		for(IOCListener l : listeners){
			l.listenConstruct(con, parameterValues);
		}
		try {
			return con.newInstance(parameterValues);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * construct with given class
	 * <br/>it get constructor from constructor filter chain, and then get parameters from param handler chain.
	 * <br/> finally call construct(Constructror&lt;?&gt; , Object[])
	 * @param cls class to construct
	 * @return instance of the class
	 */
	protected Object constructObject(Class<?> cls) {
		for(IOCListener l : listeners){
			l.listenConstructObject(cls);
		}
		Set<Annotation> set = new HashSet<Annotation>();
		for (Constructor<?> cons : cls.getConstructors()) {
			for (Annotation ann : cons.getAnnotations()) {
				set.add(ann);
			}
		}
		ConstructorFilterChain chain = new ConstructorFilterChain(constructorFilters, set);
		Constructor<?> con = chain.next().handle(cls.getConstructors(), chain);
		Object[] pv = new Object[con.getParameterTypes().length];
		for (int i = 0; i < pv.length; ++i) {
			ParamHandlerChain chain2 = new ParamHandlerChain(paramAnnotationHandlers, con
					.getParameterAnnotations()[i]);
			pv[i] = chain2.next().handle(con, con.getParameterTypes()[i], con.getParameterAnnotations()[i],
					chain2);
		}
		return construct(con, pv);
	}
	
	/**
	 * get instance with class.
	 * <br/>it would check whether it IsSingleton
	 * <br/>then invoke constructObject(Class&lt;?&gt;) to get instance
	 * @param cls class to instantiate
	 * @return instance of the class.
	 */
	protected Object getObject(Class<?> cls) {
		for(IOCListener l : listeners){
			l.listenGetObject(cls);
		}
		Annotation[] anns = cls.getAnnotations();
		boolean isSingleton = false;
		for (Annotation ann : anns) {
			if (ann.annotationType() == Singleton.class) {
				isSingleton = true;
				break;
			}
		}
		if (isSingleton) {
			if (!singletons.containsKey(cls)) {
				return constructObject(cls);
			}
			return singletons.get(cls);
		} else {
			return constructObject(cls);
		}
	}
	protected Object retrieveConstant(Class<?> type){
		Collection<Object> objects=constants.values();
		for(Object o:objects){
			if(type.isInstance(o)) return o;
		}
		return null;
	}
	
	protected static Object invokeMethod(Method method, Object target){
		Object[] pv=new Object[method.getParameterTypes().length];
		for(int i=0;i<pv.length;++i){
			ParamHandlerChain chain2 = new ParamHandlerChain(paramAnnotationHandlers, method
					.getParameterAnnotations()[i]);
			pv[i] = chain2.next().handle(method, method.getParameterTypes()[i], method.getParameterAnnotations()[i],
					chain2);
		}
		try{
			return method.invoke(target, pv);
		}catch(Exception e){
			throw new IOCException(e);
		}
	}
}
