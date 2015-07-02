package cass.toolbox.advance.inh;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class MultipleInheritable implements MIInterface {
	@Override
	public final Extends sup(String name){
		Field[] fields=this.getClass().getDeclaredFields();
		for(Field f:fields){
			if(!Modifier.isStatic(f.getModifiers())
					&& Extends.class==f.getType()){
				f.setAccessible(true);
				try{
					Extends ex=(Extends)f.get(this);
					if(ex.getC().getName().equals(name) || ex.getC().getSimpleName().equals(name)) return ex;
				}catch(Exception e){
					throw new InheritanceRuntimeException(e);
				}finally{
					f.setAccessible(false);
				}
			}
		}
		throw new InheritanceRuntimeException("No matched super class found.");
	}
}
