package cass.toolbox.advance.hotplugging.core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import cass.toolbox.advance.hotplugging.core.aware.ClassInfoAware;
import cass.toolbox.advance.hotplugging.core.exception.CallingUninitializedFieldException;
import cass.toolbox.advance.hotplugging.core.exception.CanNotInstallClassException;
import cass.toolbox.advance.hotplugging.core.exception.ClassNotInstalledException;
import cass.toolbox.advance.hotplugging.core.exception.DependencyNotFoundException;
import cass.toolbox.advance.hotplugging.core.exception.UninstallingUnsuccessfulException;
import cass.toolbox.advance.hotplugging.core.exception.UpdateUnsuccessfulException;

/**
 * 
 * @author wkgcass
 * @version 0.0.1
 *
 */
public class ClassLoaderManager implements ClassInfoAware {
	/**
	 * the class has been installed.
	 */
	public static final int INSTALLED = 0 ;
	/**
	 * the class need to be installed and can not use before installing.
	 */
	public static final int NOT_INSTALLED = 2 ;
	/**
	 * the class is not in mapping.
	 */
	public static final int NOT_MAPPED_CLASS = 1 ;
	/**
	 * the class has been registered.
	 */
	public static final int REGISTERED = 3 ;
	
	private ClassFileDecoder decoder=null;
	private Map<String,Set<String>> dependency=new HashMap<String,Set<String>>();
	private Map<String,HotpluggingLoader> loaders=new HashMap<String,HotpluggingLoader>();
	private ClassInfoMapping mapping;
	private Map<ClassLoader,Vector<Class<?>>> registeredLoaders=new HashMap<ClassLoader,Vector<Class<?>>>();
	private Set<ClassLoaderManager> registeredManagers=new HashSet<ClassLoaderManager>(); 
	private boolean autoInstall=false;
	
	protected HotpluggingLoader getInstance(String name) throws Exception{
		return new HotpluggingLoader(name,this);
	}
	
	/**
	 * Retrieve a Class object from loaders this manager manages.
	 * @param name name of the class
	 * @return named Class object.
	 * @throws Exception
	 * <br><ul><b>ClassNotInstalledException</b>, thrown when the class has not beend installed
	 * <br/><b> ClassNotFoundException</b>, thrown when loading fails
	 * <br/><b>CallingUninitializedFieldException</b>, thrown when filed not initialized, invoke setters to solve.</ul>
	 */
	public Class<?> getClass(String name) throws Exception{
		return this.getClass(null, name);
	}
	
	/**
	 * Retrieve a Class object from loaders this manager manages.
	 * <br/> <b>Only call this method from class loaders</b>
	 * @param caller the class calls for the named class.
	 * @param name name of the class to get.
	 * @return named Class object.
	 * @throws Exception
	 * <br><ul><b>ClassNotInstalledException</b>, thrown when the class has not been installed
	 * <br/><b> ClassNotFoundException</b>, thrown when loading fails
	 * <br/><b>CallingUninitializedFieldException</b>, thrown when filed not initialized, invoke setters to solve.</ul>
	 */
	public Class<?> getClass(String caller, String name) throws Exception{
		this.validate();
		
		ClassLoader ret=this.getRegisteredLoader(name);
		if(null!=ret) return ret.loadClass(name);
		
		ClassLoaderManager manager=this.getRegisteredManager(name);
		if(null!=manager) return manager.getClass(name);
		
		Class<?> cls=null;
		try{
			cls = this.loaders.get(name).loadClass(name, true); //用已安装的loader生成class
			if(null!=caller && null!=this.mapping.getPath(caller)){
				this.dependency.get(name).add(caller); //设置依赖关系
			}
		}catch(NullPointerException e){
			//空指针异常说明没找到，如果映射里能找到说明没加载，抛出异常
			if(null==this.mapping.getPath(name)) return ClassLoader.getSystemClassLoader().loadClass(name);
			if(this.autoInstall){
				this.install(name);
				return this.getClass(name);
			}else{
				throw new ClassNotInstalledException(name);
			}
		}
		return cls;
	}
	
	private ClassLoader getRegisteredLoader(String name){
		ClassLoader ret=null;
		for(ClassLoader cl : this.registeredLoaders.keySet()){
			Vector<Class<?>> v=this.registeredLoaders.get(cl);
			for(Class<?> c : v){
				if(c.getName().equals(name)) return cl;
			}
		}
		return ret;
	}
	
	private ClassLoaderManager getRegisteredManager(String name){
		for(ClassLoaderManager m:this.registeredManagers){
			if(m.getInstalledClassNames().contains(name)) return m;
		}
		return null;
	}
	
	/**
	 * getter
	 * @return mapping
	 */
	public ClassInfoMapping getClassInfoMapping(){
		return this.mapping;
	}
	
	public ClassFileDecoder getDecoder(){
		return this.decoder;
	}
	
	/**
	 * Get classes that are related.
	 * @param name name of the class
	 * @return all related classes.
	 * @throws Exception
	 * <br/><ul><b>DependencyNotFoundException</b> thrown when named class don't have dependency recode.
	 * <br/>it may be because the class has not been installed , or the class was registered to the manager</ul>
	 */
	public Set<String> getDependency(String name) throws Exception{
		if(null==this.dependency.get(name)){
			throw new DependencyNotFoundException(name);
		}
		Set<String> ret=new HashSet<String>();
		ret.addAll(this.dependency.get(name));
		int sizeAfter=ret.size();
		int sizeBefore=0;
		while(sizeAfter!=sizeBefore){
			sizeBefore=ret.size();
			Set<String> tmp=new HashSet<String>();
			for(String s : ret){
				tmp.addAll(this.dependency.get(s));
			}
			sizeAfter=ret.size();
		}
		return ret;
	}
	
	/**
	 * Get classes that are directly related.
	 * @param name class name.
	 * @return a set containing names of classes directly related to named class.
	 * @throws Exception 
	 * <br/><ul><b>ClassNotInstalledException</b> thrown when named class has not been installed.</ul>
	 */
	public Set<String> getDirectDependency(String name) throws Exception{
		if(null==this.dependency.get(name)){
			throw new ClassNotInstalledException(name);
		}
		Set<String> ret=new HashSet<String>();
		ret.addAll(this.getDependency(name));
		return ret;
	}
	
	/**
	 * Get all the name of classes the manager loaded. Registered classes won't be in the result set.
	 * @return a set containing class names.
	 */
	public Set<String> getInstalledClassNames() {
		Set<String> ret=new HashSet<String>();
		for(String s: this.loaders.keySet()){
			ret.add(s);
		}
		return ret;
	}

	/**
	 * Get all registered classes.
	 * @return a set containing all registered classes.
	 */
	public Set<String> getRegisteredClassNames(){
		Set<String > ret=new HashSet<String>();
		for(ClassLoader cl : this.registeredLoaders.keySet()){
			Vector<Class<?>> v=this.registeredLoaders.get(cl);
			for(Class<?> c : v){
				ret.add(c.getName());
			}
		}
		for(ClassLoaderManager m:this.registeredManagers){
			ret.addAll(m.getInstalledClassNames());
		}
		return ret;
	}
	
	/**
	 * Install a class.
	 * @param name name of the class
	 * @throws Exception
	 * <br/><ul><b>CanNotInstallClassException</b>, thrown when the class has been installed
	 * <br/><b>CallingUninitializedFieldException</b>, thrown when filed not initialized, invoke setters to solve.</ul>
	 */
	public void install(String name) throws Exception{
		this.install(null, name);
	}
	
	/**
	 * Install a class. 
	 * <br/> <b>Only call this method from class loaders</b>
	 * @param caller the class calls for the named class.
	 * @param name name of the class to install.
	 * @throws Exception
	 * <br/><ul><b>CanNotInstallClassException</b>, thrown when the class has been installed
	 * <br/><b>CallingUninitializedFieldException</b>, thrown when filed not initialized, invoke setters to solve.</ul>
	 */
	public void install(String caller, String name) throws Exception{
		this.validate();
		
		//can't find in mapping, means the class will be loaded by system loader. 
		//如果映射中找不到说明是要用system loader加载的
		if(null==this.mapping.getPath(name)) return; 
		
		//if the class has been registered
		//如果已经注册了
		if(null!=this.registeredLoaders.get(name)) return;
		
		//throw exception if already installed
		//如果已经安装了就抛出异常
		if(NOT_INSTALLED!=this.isInstalled(name)) throw new CanNotInstallClassException(name+" has already been installed");
		
		//create new instance of loader
		//新建loader
		HotpluggingLoader hl=this.getInstance(name);
		this.loaders.put(name, hl);
		this.dependency.put(name, new HashSet<String>());
		
		//after instantiating, set dependency
		//设置dependency
		if(null!=caller && null!=this.mapping.getPath(caller)){
			this.dependency.get(name).add(caller);
		}
	}
	
	/**
	 * Return an integer represents the state of the class. Check the integers in ClassLoaderManager constant list.
	 * @param className name of the class
	 * @return integers representing state of the class.
	 */
	public int isInstalled(String className){
		if(null==this.mapping.getPath(className)) return NOT_MAPPED_CLASS ;
		if(null!=this.getRegisteredLoader(className)) return REGISTERED ;
		if(null!=this.getRegisteredManager(className)) return REGISTERED ;
		return null==this.loaders.get(className)?NOT_INSTALLED  : INSTALLED;
	}
	
	/**
	 * Register a class loader in order to retrieve classes loaded by other ClassLoader(s) from here.
	 * <br/> registered classes cannot be uninstalled.
	 * @param cl an instance of ClassLoader
	 * @throws Exception
	 * <br/><ul>all exceptions are related to java.reflect</ul>
	 */
	@SuppressWarnings("unchecked")
	public void register(ClassLoader cl) throws Exception{
		if(null==cl) return;
		Field classesF=ClassLoader.class.getDeclaredField("classes");
		classesF.setAccessible(true);
		Vector<Class<?>> classes=(Vector<Class<?>>) classesF.get(cl);
		classesF.setAccessible(false);
		this.registeredLoaders.put(cl, classes);
	}
	
	public void register(ClassLoaderManager manager){
		if(null==manager) return;
		this.registeredManagers.add(manager);
	}
	
	/**
	 * setter
	 */
	public void setClassInfoMapping(ClassInfoMapping mapping){
		this.mapping=mapping;
	}
	
	public void setDecoder(ClassFileDecoder decoder){
		this.decoder=decoder;
	}
	
	/**
	 * Uninstall a class.
	 * @param name name of the class.
	 * @throws Exception
	 * <br/><ul><b>UninstallingUnsuccessfulException</b> thrown when named class has not been installed.
	 * <br/><b>CallingUninitializedFieldException</b>, thrown when filed not initialized, invoke setters to solve.</ul>
	 */
	public void uninstall(String name) throws Exception{
		this.validate();
		
		if(INSTALLED!=this.isInstalled(name)) throw new UninstallingUnsuccessfulException(name);
		
		Set<String> dep = this.getDependency(name);
		this.dependency.remove(name);
		this.loaders.remove(name);
		for(String s:dep){
			this.loaders.remove(s);
		}
	}
	
	/**
	 * Update a class. Meaning uninstall the class then install it again.
	 * <br/> if installing fails, it will roll back to its original state, and throw an exception.
	 * @param name name of the class to update
	 * @throws Exception
	 * <br/><ul><b>ClassNotInstalledException</b> thrown when named class has not been installed.
	 * <br/><b>UpdateUnsuccessfulException</b> thrown when it fails to update.
	 * <br/><b>CallingUninitializedFieldException</b>, thrown when filed not initialized, invoke setters to solve.</ul>
	 */
	public void update(String name) throws Exception{
		this.validate();
		
		if(NOT_INSTALLED==this.isInstalled(name)) throw new ClassNotInstalledException(name);
		
		Set<String> tmp=this.dependency.remove(name);
		HotpluggingLoader tmp2=this.loaders.remove(name);
		try{
			this.install(name);
		}catch(Exception e){
			this.dependency.remove(name);
			this.loaders.remove(name);
			this.dependency.put(name, tmp);
			this.loaders.put(name, tmp2);
			throw new UpdateUnsuccessfulException(name,e);
		}
		this.dependency.put(name, tmp);
	}
	private void validate() throws Exception {
		if(null==this.mapping) throw new CallingUninitializedFieldException(this,"classInfoMapping");
	}
	public void setAutoInstall(boolean b){
		this.autoInstall=b;
	}
}
