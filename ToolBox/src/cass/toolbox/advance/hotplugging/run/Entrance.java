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
		if(Entrance.instance) return; //���Ѿ�ʵ�����˾�ֱ�ӷ���
		
		//��ȡ����ǰsystem loader���ص���
		Field classesF = ClassLoader.class.getDeclaredField("classes");
		classesF.setAccessible(true);
		Vector<Class<?>> classes = (Vector<Class<?>>)classesF.get(this.getClass().getClassLoader());
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.addAll(classes); //�����list��
		
		//��ȡxml�ļ�����cass.util.data.xml.XMLDataAdapter������
		XMLDataAdapter xmlds=new XMLDataAdapter( new File(configPath) );
		//���ü��ص���
		List<String> doNotLoad=(List<String>) xmlds.getProperty("hotplug.init.list(name=do-not-load)");
		//�������ļ�·��map
		Map<String,String> map=(Map<String,String>)xmlds.getProperty("hotplug.init.map(name=class)");
		//base·��
		List<String> dirs =(List<String>)xmlds.getProperty("hotplug.init.list(name=base-dirs)");
		//jar����·��
		Map<String,String> jars = (Map<String,String>)xmlds.getProperty("hotplug.init.map(name=jars)");
		//jar��·��map��keyset
		Set<String> jarset=jars.keySet();
		
		//loop������
		LoopLoader loop=new LoopLoader();
		//����cass.hotplugging.run.InitLoader
		Class<?> cls = loop.loadClass("cass.hotplugging.run.InitLoader", true);
		Object init = cls.newInstance(); //ʵ����
		loop.setLoop((ClassLoader)init); //����ѭ������
		
		//cass.hotplugging.run.InitLoader��loadClass����
		Method loadClass = init.getClass().getDeclaredMethod("loadClass", String.class, boolean.class);
		//��InitLoader����cass.hotplugging.core.ClassInfoMapping
		Class<?> mappingClass=null;
		try{
		mappingClass = (Class<?>)loadClass.invoke(init, "cass.hotplugging.core.ClassInfoMapping", true);
		}catch(InvocationTargetException e){
			throw e.getTargetException();
		}
		Object mapping=mappingClass.newInstance(); //ʵ����
		//��ȡsetDoNotLoad����
		Method setDoNotLoad = mappingClass.getMethod("setDoNotLoad", List.class);
		//��ȡaddJar����
		Method mappingJar = mappingClass.getMethod("addJar", String.class,String.class);
		//��ȡaddBaseDir����
		Method mappingDir = mappingClass.getMethod("addBaseDir", String.class);
		//��ȡsetPathAll����
		Method mappingPaths = mappingClass.getMethod("setPathAll", Map.class);
		try{
			setDoNotLoad.invoke(mapping, doNotLoad); //����setDoNotLoad
		}catch(InvocationTargetException e){
			throw e.getTargetException();
		}
		
		for(String dir : dirs){
			try{
				mappingDir.invoke(mapping, dir); //����addBaseDir
			}catch(InvocationTargetException e){
				throw e.getTargetException();
			}
		}
		for(String jar:jarset){
			try{
				mappingJar.invoke(mapping, jar , jars.get(jar)); //����addJar
			}catch(InvocationTargetException e){
				throw e.getTargetException();
			}
		}
		try{
			mappingPaths.invoke(mapping, map); //����setPathAll
		}catch(InvocationTargetException e){
			throw e.getTargetException();
		}
		
		//��ȡInitLoader��setClassInfoMapping����
		Method setClassInfoMapping = init.getClass().getMethod("setClassInfoMapping", Object.class);
		try{
			setClassInfoMapping.invoke(init, mapping); //����setClassInfoMapping����
		}catch(InvocationTargetException e){
			throw e.getTargetException();
		}
		
		//����cass.hotplugging.core.ClassLoaderManager
		Class<?> clmClass=null;
		try{
			clmClass =(Class<?>) loadClass.invoke(init,"cass.hotplugging.core.ClassLoaderManager", true);
		}catch(InvocationTargetException e){
			throw e.getTargetException();
		}
		//ʵ����ClassLoaderManager
		Object clm=clmClass.newInstance();
		
		//�Ž�Starter��map����������
		Entrance.map.put("manager", clm );
		Entrance.map.put("cass.hotplugging.core.ClassLoaderManager", clm );
		Entrance.map.put("mapping", mapping);
		Entrance.map.put("cass.hotplugging.core.ClassInfoMapping", mapping);
		
		//��ȡload-at-init
		List<String> loadAtInit=(List<String>)xmlds.getProperty("hotplug.init.list(name=load-at-init)");
		//�����ǽ��м��غ�ʵ����
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
		
		
		
		//��ȡaware
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
		
		
		//��Manager��ע��InitLoader
		Method register=clmClass.getMethod("register", ClassLoader.class);
		try{
			register.invoke(clm, init);
		}catch(InvocationTargetException e){
			throw e.getTargetException();
		}
		
		Entrance.instance=true; //�����ɹ�
		
		//��ȡmanager��isInstalled��install��getClass����
		Method isInstalled=clmClass.getMethod("isInstalled", String.class);
		Method install=clmClass.getMethod("install", String.class);
		Method getClass=clmClass.getMethod("getClass",String.class);
		
		//��ȡinvocations
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
		
		//ɾ��������ж�����ص��ࣨ���ٻ�ɾȥLoopLoader,XMLDataSource��
		Iterator<Class<?>> it=classes.iterator();
		while(it.hasNext()){
			Class<?> tmp=it.next();
			if(!list.contains(tmp)){
				it.remove();
			}
		}
		
		classesF.setAccessible(false); //����accessible
		
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
