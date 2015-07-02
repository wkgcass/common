package cass.toolbox.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class MethodDeposit extends StaticMethodDeposit {
	private Object target;
	public MethodDeposit(Class<?> dep , boolean addAll){
		super(dep,false);
		try{
			this.target=dep.newInstance();
		}catch(Exception e){
			throw new IllegalArgumentException();
		}
		if(addAll){
			this.addAll();
		}
	}
	public MethodDeposit(Class<?> dep){
		this(dep,true);
	}
	public MethodDeposit(Object target, boolean addAll){
		super(target.getClass(), false);
		this.target=target;
		if(addAll){
			this.addAll();
		}
	}
	public MethodDeposit(Object target){
		this(target,true);
	}
	
	@Override
	public void add(Method m){
		if(!Reflection.canAccessWithThisPointer(this.target.getClass(), m)){
			throw new IllegalArgumentException("Not accessible with this pointer.");
		}
		Set<Method> set=methods.get(m.getName());
		if(null==set){
			set=new HashSet<Method>();
			methods.put(m.getName(), set);
		}
		set.add(m);
	}
	
	@Override
	protected Object invoke(Method method,Object... pv) throws IllegalAccessException, InvocationTargetException{
		return method.invoke(this.target, pv);
	}
}
