package cass.toolbox.advance.hotplugging.run;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import cass.toolbox.data.xml.XMLDataAdapter;

public class Entrance {
	
	private static Map<String,Object> map=new HashMap<String,Object>();
	public static LinkedHashMap<Class<?>,Map<Method,Object[]>> awares=new LinkedHashMap<Class<?>,Map<Method,Object[]>>();
	
	private static boolean instance=false;
	
	public Entrance() throws Throwable{
		this(System.getProperty("user.dir")+"/mapping.xml");
	}
	
	@SuppressWarnings("unchecked")
	public Entrance(String configPath) throws Throwable{
		if(Entrance.instance) return; //若已经实例化了就直接返回
		
		//获取运行前system loader加载的类
		Field classesF = ClassLoader.class.getDeclaredField("classes");
		classesF.setAccessible(true);
		Vector<Class<?>> classes = (Vector<Class<?>>)classesF.get(this.getClass().getClassLoader());
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.addAll(classes); //存放在list中
		
		//获取xml文件，用cass.util.data.xml.XMLDataAdapter解析它
		XMLDataAdapter xmlds=new XMLDataAdapter( new File(configPath) );
		//不用加载的类
		List<String> doNotLoad=(List<String>) xmlds.getProperty("hotplug.init.list(name=do-not-load)");
		//单个类文件路径map
		Map<String,String> map=(Map<String,String>)xmlds.getProperty("hotplug.init.map(name=class)");
		//base路径
		List<String> dirs =(List<String>)xmlds.getProperty("hotplug.init.list(name=base-dirs)");
		//jar包的路径
		Map<String,String> jars = (Map<String,String>)xmlds.getProperty("hotplug.init.map(name=jars)");
		//jar包路径map的keyset
		Set<String> jarset=jars.keySet();
		
		//loop加载器
		LoopLoader loop=new LoopLoader();
		//加载cass.hotplugging.run.InitLoader
		Class<?> cls = loop.loadClass("cass.hotplugging.run.InitLoader", true);
		Object init = cls.newInstance(); //实例化
		loop.setLoop((ClassLoader)init); //设置循环加载
		
		//cass.hotplugging.run.InitLoader的loadClass方法
		Method loadClass = init.getClass().getDeclaredMethod("loadClass", String.class, boolean.class);
		//用InitLoader加载cass.hotplugging.core.ClassInfoMapping
		Class<?> mappingClass=null;
		try{
		mappingClass = (Class<?>)loadClass.invoke(init, "cass.hotplugging.core.ClassInfoMapping", true);
		}catch(InvocationTargetException e){
			throw e.getTargetException();
		}
		Object mapping=mappingClass.newInstance(); //实例化
		//获取setDoNotLoad方法
		Method setDoNotLoad = mappingClass.getMethod("setDoNotLoad", List.class);
		//获取addJar方法
		Method mappingJar = mappingClass.getMethod("addJar", String.class,String.class);
		//获取addBaseDir方法
		Method mappingDir = mappingClass.getMethod("addBaseDir", String.class);
		//获取setPathAll方法
		Method mappingPaths = mappingClass.getMethod("setPathAll", Map.class);
		try{
			setDoNotLoad.invoke(mapping, doNotLoad); //调用setDoNotLoad
		}catch(InvocationTargetException e){
			throw e.getTargetException();
		}
		
		for(String dir : dirs){
			try{
				mappingDir.invoke(mapping, dir); //调用addBaseDir
			}catch(InvocationTargetException e){
				throw e.getTargetException();
			}
		}
		for(String jar:jarset){
			try{
				mappingJar.invoke(mapping, jar , jars.get(jar)); //调用addJar
			}catch(InvocationTargetException e){
				throw e.getTargetException();
			}
		}
		try{
			mappingPaths.invoke(mapping, map); //调用setPathAll
		}catch(InvocationTargetException e){
			throw e.getTargetException();
		}
		
		//获取InitLoader的setClassInfoMapping方法
		Method setClassInfoMapping = init.getClass().getMethod("setClassInfoMapping", Object.class);
		try{
			setClassInfoMapping.invoke(init, mapping); //调用setClassInfoMapping方法
		}catch(InvocationTargetException e){
			throw e.getTargetException();
		}
		
		//加载cass.hotplugging.core.ClassLoaderManager
		Class<?> clmClass=null;
		try{
			clmClass =(Class<?>) loadClass.invoke(init,"cass.hotplugging.core.ClassLoaderManager", true);
		}catch(InvocationTargetException e){
			throw e.getTargetException();
		}
		//实例化ClassLoaderManager
		Object clm=clmClass.newInstance();
		
		//放进Starter的map里用来调用
		Entrance.map.put("manager", clm );
		Entrance.map.put("cass.hotplugging.core.ClassLoaderManager", clm );
		Entrance.map.put("mapping", mapping);
		Entrance.map.put("cass.hotplugging.core.ClassInfoMapping", mapping);
		
		//获取load-at-init
		List<String> loadAtInit=(List<String>)xmlds.getProperty("hotplug.init.list(name=load-at-init)");
		//对他们进行加载和实例化
		for(String s : loadAtInit){
			if(s.startsWith("cass.hotplugging.core.")) continue;
			Class<?> clazz=null;
			try{
				clazz = (Class<?>) loadClass.invoke(init,s, true);
			}catch(InvocationTargetException e){
				throw e.getTargetException();
			}
			Object inst = clazz.newInstance();
			Entrance.map.put(s, inst);
		}
		
		
		
		//获取aware
		List<Map<String,?>> aware=(List<Map<String,?>>)xmlds.getProperty("hotplug.init.list(name=aware)");
		for(Map<String,?>m : aware){
			String interfName=(String) m.get("interface");
			Class<?> interfClass=null;
			try{
				interfClass= (Class<?>) loadClass.invoke(init, interfName,true);
			}catch(InvocationTargetException e){
				throw e.getTargetException();
			}
			
			Entrance.awares.put(interfClass, new HashMap<Method,Object[]>());
			
			List<Map<String,?>> methods=(List<Map<String,?>>) m.get("method");
			for(Map<String,?> met : methods){
				String methodName=(String) met.get("name");
				List<String> param=(List<String>) met.get("param");
				Class<?>[] clazz=new Class[param.size()];
				Object[] objs=new Object[param.size()];
				for(int i=0;i<clazz.length;++i){
					try{
					clazz[i]=(Class<?>) loadClass.invoke(init, param.get(i),true);
					}catch(InvocationTargetException e){
						throw e.getTargetException();
					}
					objs[i]=Entrance.map.get(param.get(i));
				}
				Method method=interfClass.getMethod(methodName, clazz);
				
				Entrance.awares.get(interfClass).put(method,objs);
				
				for(String key:Entrance.map.keySet()){
					Object o = Entrance.map.get(key);
					if(interfClass.isInstance(o)){
						try{
							method.invoke(o, objs);
						}catch(InvocationTargetException e){
							throw e.getTargetException();
						}
					}
				}
			}
		}
		
		
		//在Manager中注册InitLoader
		Method register=clmClass.getMethod("register", ClassLoader.class);
		try{
			register.invoke(clm, init);
		}catch(InvocationTargetException e){
			throw e.getTargetException();
		}
		
		Entrance.instance=true; //启动成功
		
		//获取manager的isInstalled，install，getClass方法
		Method isInstalled=clmClass.getMethod("isInstalled", String.class);
		Method install=clmClass.getMethod("install", String.class);
		Method getClass=clmClass.getMethod("getClass",String.class);
		
		//获取invocations
		List<Map<String,String>> invocations=(List<Map<String,String>>)xmlds.getProperty("hotplug.launch.list(name=invocations)");
		for(Map<String,String> m : invocations){
			String className=m.get("class");
			String methodName=m.get("static-method");
			//List<Map<?,?>> param=(List<Map<?,?>>)m.get("param");
			Class<?> clazz=null;
			try{
				if(2!=(Integer) isInstalled.invoke(clm, className)){
					clazz=(Class<?>) getClass.invoke(clm, className);
				}else{
					install.invoke(clm, className);
					clazz=(Class<?>) getClass.invoke(clm, className);
				}
			}catch(InvocationTargetException e){
				throw e.getTargetException();
			}
			
			Method method=clazz.getMethod(methodName, new Class[0]);

			Thread t=new Thread(new ThreadX(method));
			t.start();
			
		}
		
		//删除这过程中多余加载的类（至少会删去LoopLoader,XMLDataSource）
		Iterator<Class<?>> it=classes.iterator();
		while(it.hasNext()){
			Class<?> tmp=it.next();
			if(!list.contains(tmp)){
				it.remove();
			}
		}
		
		classesF.setAccessible(false); //设置accessible
		
	}
	public static Object getObject(String name){
		return Entrance.map.get(name);
	}
}

class ThreadX implements Runnable{
	private Method m;
	public ThreadX(Method m){
		this.m=m;
	}

	@Override
	public void run() {
		try {
			m.invoke(null, new Object[0]);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
		}
	}
	
}
