package cass.toolbox.advance.hotplugging.island;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cass.toolbox.advance.hotplugging.run.Entrance;

public class IslandHandler implements InvocationHandler {
	
	/**
	 * target object to do proxy on
	 */
	private Object target=null;
	/**
	 * if destroyed is set to true, all method would return null.
	 */
	private boolean destroyed=false;
	
	private Class<?>[] parameterTypes;
	private Object[] parameterValues;
	
	public void destroy() {
		this.target=null;
		this.destroyed=true;
		this.parameterTypes=null;
		this.parameterValues=null;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if(destroyed) return null;
		Object ret=method.invoke(this.target, args); 
		return ret;
	}

	public Object bind() {
		if(this.destroyed) return null;
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);
	}

	public void update(Class<?> clazz) throws Exception {
		if(this.parameterTypes==null){
			this.target=clazz.newInstance();
		}else{
			Constructor<?> con=clazz.getDeclaredConstructor(parameterTypes);
			this.target=con.newInstance(parameterValues);
		}
		for(Class<?> c:Entrance.awares.keySet()){
			if(c.isInstance(this.target)){
				for(Method m:Entrance.awares.get(c).keySet()){
					m.invoke(target, Entrance.awares.get(c).get(m));
				}
			}
		}
	}

	public void setParameters(Class<?>[] parameterTypes,
			Object[] parameterValues) {
		if(this.destroyed) return;
		this.parameterTypes=parameterTypes;
		this.parameterValues=parameterValues;
	}

	public IslandHandler cloneWithSameConfiguration() {
		if(this.destroyed){
			return null;
		}
		IslandHandler is=new IslandHandler();
		is.setParameters(parameterTypes, parameterValues);
		return is;
	}

}
