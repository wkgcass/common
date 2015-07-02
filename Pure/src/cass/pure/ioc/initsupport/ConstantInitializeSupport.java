package cass.pure.ioc.initsupport;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;



import cass.pure.init.InitializeSupport;
import cass.pure.ioc.AutoWire;
import cass.pure.ioc.IOCController;

public class ConstantInitializeSupport implements InitializeSupport {
	
	private Set<Class<?>> classes=new HashSet<Class<?>>();
	
	@Override
	public void addClass(Class<?> cls) {
		if(null!=cls.getAnnotation(ConstantStorage.class)){
			classes.add(cls);
		}
	}

	@Override
	public void doInit(String[] args) {
		for(Class<?> cls : classes){
			try{
				Object inst=AutoWire.get(cls);
				for(Field f : inst.getClass().getDeclaredFields()){
					f.setAccessible(true);
					IOCController.registerConstant(f.getName(), f.get(inst));
					f.setAccessible(false);
				}
			}catch(Exception e){
				continue;
			}
		}
	}

	@Override
	public double priority() {
		return -2.2;
	}

}
