package cass.toolbox.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cass.toolbox.ioc.annotations.Bean;
import cass.toolbox.ioc.annotations.IsSingleton;
import cass.toolbox.ioc.annotations.constructor.*;
import cass.toolbox.ioc.annotations.param.*;
import cass.toolbox.ioc.annotations.setter.*;
import cass.toolbox.ioc.annotations.type.*;
import cass.toolbox.ioc.listener.IOCListener;
import cass.toolbox.util.LocalClassRetriever;

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
 * @see cass.toolbox.ioc.annotations.type.TypeHandlerChain
 * @see cass.toolbox.ioc.annotations.type.TypeAnnotationHandler
 * @see cass.toolbox.ioc.annotations.type.TypeDefaultHandler
 * @see cass.toolbox.ioc.annotations.type.TypeIsSingletonHandler
 * @see cass.toolbox.ioc.annotations.type.TypeWireHandler
 * 
 * @see cass.toolbox.ioc.annotations.constructor.ConstructorFilterChain
 * @see cass.toolbox.ioc.annotations.constructor.ConstructorFilter
 * @see cass.toolbox.ioc.annotations.constructor.ConstructorDefaultFilter
 * @see cass.toolbox.ioc.annotations.constructor.DefaultConstructorFilter
 * 
 * @see cass.toolbox.ioc.annotations.param.ParamHandlerChain
 * @see cass.toolbox.ioc.annotations.param.ParamAnnotationHandler
 * @see cass.toolbox.ioc.annotations.param.DefaultParamHandler
 * @see cass.toolbox.ioc.annotations.param.ParamForceHandler
 * @see cass.toolbox.ioc.annotations.param.ParamUseHandler
 * @see cass.toolbox.ioc.annotations.param.PrimitiveParameterHandler
 * 
 * @see cass.toolbox.ioc.annotations.setter.SetterHandlerChain
 * @see cass.toolbox.ioc.annotations.setter.SetterAnnotationHandler
 * @see cass.toolbox.ioc.annotations.setter.DefaultSetterHandler
 * @see cass.toolbox.ioc.annotations.setter.SetterForceHandler
 * @see cass.toolbox.ioc.annotations.setter.SetterIgnoreHandler
 * @see cass.toolbox.ioc.annotations.setter.SetterUseHandler
 *
 */
public abstract class IOCController {
	/**
	 * class retriever, get all classes in order to get annotations.
	 */
	private static LocalClassRetriever classes=new LocalClassRetriever();
	/**
	 * param annotation handlers
	 */
	private static Set<ParamAnnotationHandler> ahs = new HashSet<ParamAnnotationHandler>();
	/**
	 * maps beanName to beanClass
	 */
	private static Map<String, Class<?>> beanmap = new HashMap<String, Class<?>>();
	/**
	 * singleton beans.
	 */
	private static Map<String, Object> beans = new HashMap<String, Object>();
	/**
	 * constructor filters
	 */
	private static Set<ConstructorFilter> cahs = new HashSet<ConstructorFilter>();
	/**
	 * classes that are constructing
	 */
	private static Set<Class<?>> constructing = new HashSet<Class<?>>();
	/**
	 * beans that are constructing
	 */
	private static Map<Class<?>, String> constructingBean = new HashMap<Class<?>, String>();
	/**
	 * setter annotation handlers
	 */
	private static Set<SetterAnnotationHandler> handlers = new HashSet<SetterAnnotationHandler>();
	/**
	 * singleton class instances
	 */
	private static Map<Class<?>, Object> objects = new HashMap<Class<?>, Object>();
	/**
	 * type annotation handler
	 */
	private static Set<TypeAnnotationHandler> tahs = new HashSet<TypeAnnotationHandler>();
	/**
	 * listeners for 8 main methods
	 */
	private static Set<IOCListener> listeners=new HashSet<IOCListener>();
	
	static {
		
		//register param handlers
		register(new ParamForceHandler());
		register(new ParamUseHandler());
		register(new DefaultParamHandler());
		register(new PrimitiveParameterHandler());
		
		//register type handlers
		register(new DefaultTypeHandler());
		register(new TypeDefaultHandler());
		register(new TypeIsSingletonHandler());
		register(new TypeWireHandler());
		
		//register setter handlers
		register(new DefaultSetterHandler());
		register(new SetterForceHandler());
		register(new SetterIgnoreHandler());
		register(new SetterUseHandler());
		
		//register constructor filters
		register(new DefaultConstructorFilter());
		register(new ConstructorDefaultFilter());
		
		//get classes from default project path.
		collectBeanInfo(IOCController.class.getClassLoader(),
				IOCController.class.getResource("/").toString().replace("%20", " ").substring(6),
				true);
	}

	/**
	 * block the constructing process
	 * @param toConstruct class to construct
	 */
	static void block(Class<?> toConstruct) {
		if(AutoWire.DEBUG_MODE){
		System.out.println("IOCController.block("+toConstruct+")");
		}
		
		while (constructing.contains(toConstruct)) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}
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
		SetterHandlerChain chain = new SetterHandlerChain(handlers, annset);
		try{
			chain.next().handle(target, m, annset, chain);
		}catch(CircularDependencyError e){
			IsSingleton is=target.getClass().getAnnotation(IsSingleton.class);
			Bean b=target.getClass().getAnnotation(Bean.class);
			if(null==is && (b==null || !b.isSingleton() ) && "".equals(nameOfConstructingBean(beanmap.get(b.name()[0])))){
				throw e;
			}
			if(null==e.getBean()){
				setFutureTask(target,m,e.getCls());
			}else{
				setFutureTask(target,m,e.getBean());
			}
		}
	}
	
	private static void setFutureTask(final Object target,final Method setter,final String bean){
		Thread t=new Thread(
				new Runnable(){
					public void run(){
						while(!beans.containsKey(bean)){
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {}
						}
						try{
							setter.invoke(target, beans.get(bean));
						}catch(Exception e){e.printStackTrace();return;}
					}
				}
		);
		
		t.start();
	}
	private static void setFutureTask(final Object target,final Method setter,final Class<?> cls){
		Thread t=new Thread(
				new Runnable(){
					public void run(){
						while(!objects.containsKey(cls)){
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {}
						}
						try{
							setter.invoke(target, objects.get(cls));
						}catch(Exception e){e.printStackTrace();return;}
					}
				}
		);
		
		t.start();
	}

	/**
	 * check whether a class is currently constructing.
	 * @param cls class to check
	 * @return true if is constructing, false otherwise
	 */
	protected static boolean isConstructing(Class<?> cls) {
		if(AutoWire.DEBUG_MODE){
		System.out.println("IOCController.isConstructing("+cls+")");
		}
		return constructing.contains(cls);
	}

	public static void register(ConstructorFilter ah) {
		cahs.add(ah);
	}

	public static void register(ParamAnnotationHandler ah) {
		ahs.add(ah);
	}

	public static void register(SetterAnnotationHandler ah) {
		handlers.add(ah);
	}

	public static void register(TypeAnnotationHandler ah) {
		tahs.add(ah);
	}

	/**
	 * when constructing process finishes , called by AutoWire
	 * @see cass.toolbox.ioc.AutoWire
	 * @param cls the class that ends its constructing process.
	 */
	static void removeConstructing(Class<?> cls) {
		if(AutoWire.DEBUG_MODE){
		System.out.println("IOCController.removeConstructing("+cls+")");
		}
		synchronized (constructing) {
			constructing.remove(cls);
		}
	}

	/**
	 * when constructing process starts, called by AutoWire
	 * @see cass.toolbox.ioc.AutoWire
	 * @param cls the class that start its constructing process.
	 */
	static void setConstructing(Class<?> cls) {
		if(AutoWire.DEBUG_MODE){
		System.out.println("IOCController.setConstructing("+cls+")");
		}
		synchronized (constructing) {
			constructing.add(cls);
		}
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
		if(isConstructing(con.getDeclaringClass())){
			String bean=nameOfConstructingBean(con.getDeclaringClass());
			if(bean.equals("")){
				throw new CircularDependencyError(con.getDeclaringClass());
			}else{
				throw new CircularDependencyError(bean);
			}
		}
		boolean setConstructing=false;
		if(con.getDeclaringClass().getAnnotation(IsSingleton.class)!=null) setConstructing=true;
		if(!setConstructing){
			Bean b=con.getDeclaringClass().getAnnotation(Bean.class);
			if(null!=b && b.isSingleton() && !"".equals(nameOfConstructingBean(con.getDeclaringClass()))) setConstructing=true;
		}
		if(setConstructing){
			setConstructing(con.getDeclaringClass());
		}
		try {
			return con.newInstance(parameterValues);
		} catch (IllegalArgumentException e) {
			return null;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (InvocationTargetException e) {
			Throwable t=e.getTargetException();
			if(t instanceof Error){
				throw (Error)t;
			}else{
				return null;
			}
		}finally{
			if(!AutoWire.class.isAssignableFrom(con.getDeclaringClass())){
				removeConstructing(con.getDeclaringClass());
			}
		}
	}

	/**
	 * construct a bean with given name
	 * <br/>this method get class from beanmap, and invoke get(Class<?>)
	 * @param beanname name of the bean to costruct
	 * @return new instance of the bean
	 */
	protected Object constructBean(String beanname) {
		for(IOCListener l : listeners){
			l.listenConstructBean(beanname);
		}
		Class<?> beanClass = beanmap.get(beanname);
		constructingBean.put(beanClass, beanname);
		try{
			return get(beanClass);
		}finally{
			constructingBean.remove(beanClass);
		}
	}

	/**
	 * check which bean of a class is constructing
	 * @param cls the class of bean to be checked
	 * @return name of the bean, "" if no bean of the class is being constructing.
	 */
	protected static String nameOfConstructingBean(Class<?> cls) {
		if(AutoWire.DEBUG_MODE){
		System.out.println("IOCController.nameOfConstructingBean("+cls+")");
		}
		String ret = constructingBean.get(cls);
		if (null == ret)
			return "";
		return constructingBean.get(cls);
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
		ConstructorFilterChain chain = new ConstructorFilterChain(cahs, set);
		Constructor<?> con = chain.next().handle(cls.getConstructors(), chain);
		Object[] pv = new Object[con.getParameterTypes().length];
		for (int i = 0; i < pv.length; ++i) {
			ParamHandlerChain chain2 = new ParamHandlerChain(ahs, con
					.getParameterAnnotations()[i]);
			pv[i] = chain2.next().handle(con, con.getParameterTypes()[i], con.getParameterAnnotations()[i],
					chain2);
		}
		return construct(con, pv);
	}

	/**
	 * get instance by class.
	 * <br/>this method would check type handler chain to generate instance.
	 * <br/>beans are not recommended to retrieve with this method, use getBean(String) instead.
	 * @param cls
	 * @return
	 */
	protected Object get(Class<?> cls) {
		for(IOCListener l : listeners){
			l.listenGet(cls);
		}
		TypeHandlerChain chain = new TypeHandlerChain(tahs, cls
				.getAnnotations());
		return chain.next().handle(cls, chain);
	}

	/**
	 * get instance by bean name
	 * <br/>it would check whether the bean isSingleton
	 * <br/>then invoke get(Class&lt;?&gt;) to get instance
	 * @param beanname name of bean to instantiate
	 * @return instance of the bean.
	 */
	protected Object getBean(String beanname) {
		for(IOCListener l : listeners){
			l.listenGetBean(beanname);
		}
		Class<?> beanClass = beanmap.get(beanname);
		Bean beanAnno = beanClass.getAnnotation(Bean.class);
		if (beanAnno.isSingleton()) {
			if (!beans.containsKey(beanname)) {
				beans.put(beanname, constructBean(beanname));
			}
			return beans.get(beanname);
		} else {
			return constructBean(beanname);
		}
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
			if (ann.annotationType() == IsSingleton.class) {
				isSingleton = true;
			}
		}
		if (isSingleton) {
			if (!objects.containsKey(cls)) {
				objects.put(cls, constructObject(cls));
			}
			return objects.get(cls);
		} else {
			return constructObject(cls);
		}
	}
	
	/**
	 * 
	 * @param cl classloader to load classes
	 * @param dir base directory
	 * @param update whether to update the current beanmap( updating consumes resource )
	 */
	public static void collectBeanInfo(ClassLoader cl, String dir, boolean update){
		classes.addBaseDir(dir);
		if(update){
			synchronized(beanmap){
				beanmap=new HashMap<String,Class<?>>();
				Set<String> set=classes.getMapping().keySet();
				for(String s:set){
					try {
						Class<?> cls=cl.loadClass(s);
						Bean bean=cls.getAnnotation(Bean.class);
						if(null==bean) continue;
						for(String name:bean.name()){
							beanmap.put(name, cls);
						}
					} catch (ClassNotFoundException e) {
						continue;
					}
				}
			}
		}
	}
	
	public static void register(IOCListener listener){
		listeners.add(listener);
	}
}
