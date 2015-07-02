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
	 * manager����
	 */
	private ClassLoaderManager clm=null;
	
	/**
	 * maps class name to proxy handlers.
	 * һ������������handler��ӳ��
	 */
	private Map<String, List<IslandHandler> > ctp;
	/**
	 * handler��ģ�壬��cloneWithSameConfiguration��������handler
	 */
	private Map<String,IslandHandler> templates;
	
	public ObjectManager(){
		this.ctp=new HashMap<String, List<IslandHandler> >();
		this.templates=new HashMap<String,IslandHandler>();
		this.defaultTemplate=new IslandHandler();
	}
	
	//���Ϸ�
	private void validate() throws Exception{
		if(null==clm) throw new CallingUninitializedFieldException(this,"classLoaderManager");
	}

	/**
	 * create new instance of registered instance.
	 * <br/>����һ����ʵ��
	 * @param registeredClassName name of registered class. ����
	 * @return new instance of the object (it is a proxy object). ���ɵ�ʵ�������ʵ����Proxy����
	 * @throws Exception
	 */
	public Object newInstance(String className) throws Exception {
		validate();
		
		//�ӹµ�handlerģ���map��ȡ����Ӧhandler
		IslandHandler ih=this.templates.get(className);
		
		//��������ھ�ʹ��Ĭ��ģ��
		if(null==ih) ih=this.defaultTemplate;
		
		//��ģ��������handler
		IslandHandler newIh=ih.cloneWithSameConfiguration();
		
		//�����¶���
		newIh.update(this.clm.getClass(className));
		
		//ȡ����Ӧ��Ĺµ�handler�б����������ɵ�handler
		List<IslandHandler> list=this.ctp.get(className);
		if(null==list) {
			this.ctp.put(className, new LinkedList<IslandHandler>());
			list=this.ctp.get(className);
		}
		list.add(newIh);
		
		//����Proxy���󲢷���
		return newIh.bind();
	}

	/**
	 * disable all instances of a class. 
	 * <br/>Note that: this class will be deleted from register list.
	 * <br/>����һ���������ʵ��
	 * <br/>ע�⣬���������ע���б���ɾȥ
	 * @param className name of the class Ҫ���ٵ�����
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
	 * <br/>������װ�ص����������ɸ�������
	 * @param className name of the class Ҫ���µ�����
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
	 * <br/>��set��ÿһ��ֵ����destroy(String)
	 * @param infectedClasses set containing class names ����������set
	 */
	public void destroyAll(Set<String> infectedClasses) {
		for(String s:infectedClasses){
			this.destroy(s);
		}
	}

	/**
	 * call update(String) for every string in the set
	 * <br/>��set��ÿһ��ֵ����update(String)
	 * @param infectedClasses set containing class names ����������set
	 */
	public void updateAll(Set<String> infectedClasses) throws Exception {
		for(String s:infectedClasses){
			this.update(s);
		}
	}

	/**
	 * Register a class.
	 * <br/>ע��һ����
	 * @param className name of the class ����
	 * @param parameterTypes constructor param types. can be set to null. �������������ͣ�������Ϊnull
	 * @param parameterValues constructor param values. can be set to null. ����������ֵ��������Ϊnull
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
