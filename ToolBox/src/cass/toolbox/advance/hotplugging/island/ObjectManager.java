package cass.toolbox.advance.hotplugging.island;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cass.toolbox.advance.hotplugging.core.aware.CLManagerAware;
import cass.toolbox.advance.hotplugging.core.exception.CallingUninitializedFieldException;
import cass.toolbox.advance.hotplugging.core.ClassLoaderManager;

public class ObjectManager implements CLManagerAware {
	
	private IslandHandler defaultTemplate;
	
	/**
	 * class loader manager, class objects are produced from here.
	 * manager对象
	 */
	private ClassLoaderManager clm=null;
	
	/**
	 * maps class name to proxy handlers.
	 * 一个类名到代理handler的映射
	 */
	private Map<String, List<IslandHandler> > ctp;
	/**
	 * handler的模板，用cloneWithSameConfiguration快速制作handler
	 */
	private Map<String,IslandHandler> templates;
	
	public ObjectManager(){
		this.ctp=new HashMap<String, List<IslandHandler> >();
		this.templates=new HashMap<String,IslandHandler>();
		this.defaultTemplate=new IslandHandler();
	}
	
	//检查合法
	private void validate() throws Exception{
		if(null==clm) throw new CallingUninitializedFieldException(this,"classLoaderManager");
	}

	/**
	 * create new instance of registered instance.
	 * <br/>生成一个新实例
	 * @param registeredClassName name of registered class. 类名
	 * @return new instance of the object (it is a proxy object). 生成的实例（这个实例是Proxy对象）
	 * @throws Exception
	 */
	public Object newInstance(String className) throws Exception {
		validate();
		
		//从孤岛handler模板的map中取出对应handler
		IslandHandler ih=this.templates.get(className);
		
		//如果不存在就使用默认模板
		if(null==ih) ih=this.defaultTemplate;
		
		//从模板生成新handler
		IslandHandler newIh=ih.cloneWithSameConfiguration();
		
		//设置新对象
		newIh.update(this.clm.getClass(className));
		
		//取出对应类的孤岛handler列表，加入新生成的handler
		List<IslandHandler> list=this.ctp.get(className);
		if(null==list) {
			this.ctp.put(className, new LinkedList<IslandHandler>());
			list=this.ctp.get(className);
		}
		list.add(newIh);
		
		//生成Proxy对象并返回
		return newIh.bind();
	}

	/**
	 * disable all instances of a class. 
	 * <br/>Note that: this class will be deleted from register list.
	 * <br/>销毁一个类的所有实例
	 * <br/>注意，这个类会从已注册列表中删去
	 * @param className name of the class 要销毁的类名
	 */
	public void destroy(String className) {
		List<IslandHandler> list=this.ctp.get(className);
		if(null==list) return;
		for(IslandHandler ih : list){
			ih.destroy();
		}
		this.ctp.remove(className);
		this.templates.remove(className);
	}

	/**
	 * recreate instances with up-to-date loaded classes from class loader.
	 * <br/>以最新装载的类重新生成各个对象
	 * @param className name of the class 要更新的类名
	 * @throws Exception
	 */
	public void update(String className) throws Exception {
		validate();
		
		List<IslandHandler> list=this.ctp.get(className);
		if(null==list) return;
		for(IslandHandler ih : list){
			ih.update(this.clm.getClass(className));
		}
	}
	
	/**
	 * call destroy(String) for every string in the set
	 * <br/>对set中每一个值调用destroy(String)
	 * @param infectedClasses set containing class names 包含类名的set
	 */
	public void destroyAll(Set<String> infectedClasses) {
		for(String s:infectedClasses){
			this.destroy(s);
		}
	}

	/**
	 * call update(String) for every string in the set
	 * <br/>对set中每一个值调用update(String)
	 * @param infectedClasses set containing class names 包含类名的set
	 */
	public void updateAll(Set<String> infectedClasses) throws Exception {
		for(String s:infectedClasses){
			this.update(s);
		}
	}

	/**
	 * Register a class.
	 * <br/>注册一个类
	 * @param className name of the class 类名
	 * @param parameterTypes constructor param types. can be set to null. 构造器参数类型，可以设为null
	 * @param parameterValues constructor param values. can be set to null. 构造器参数值，可以设为null
	 * @throws Exception
	 */
	public void register(String className, Class<?>[] parameterTypes,
			Object[] parameterValues) throws Exception {
		validate();
		
		IslandHandler ih=new IslandHandler();
		ih.setParameters(parameterTypes, parameterValues);
		
		this.templates.put(className, ih);
		
		this.ctp.put(className, new LinkedList<IslandHandler>());
	}

	@Override
	public void setManager(ClassLoaderManager clm) {
		this.clm=clm;
	}

}
