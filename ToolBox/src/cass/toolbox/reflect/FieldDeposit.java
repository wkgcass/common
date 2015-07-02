package cass.toolbox.reflect;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class FieldDeposit extends StaticFieldDeposit {
	private Object target;
	public Set<Field> fields=new HashSet<Field>();
	public FieldDeposit(Class<?> dep, boolean addAll){
		super(dep, false);
		try{
			this.target=dep.newInstance();
		}catch(Exception e){
			throw new IllegalArgumentException("Constructor without parameters cannot be found.");
		}
		if(addAll){
			this.addAll();
		}
	}
	public FieldDeposit(Class<?> dep){
		this(dep,true);
	}
	public FieldDeposit(Object target , boolean addAll){
		super(target.getClass() ,false);
		this.target=target;
		if(addAll){
			this.addAll();
		}
	}
	public FieldDeposit(Object target){
		this(target, true);
	}
	
	@Override
	public void add(Field f){
		if(!Reflection.canAccessWithThisPointer(this.target.getClass(), f)){
			throw new IllegalArgumentException(f+" Not accessible by "+this.target.getClass()+" with (this) pointer.");
		}
		this.fields.add(f);
	}
	
	@Override
	protected Object get(Field f) throws IllegalAccessException{
		return f.get(this.target);
	}
	
}
