package cass.pure.ioc;

import java.lang.reflect.Method;

import cass.pure.ioc.annotations.Invoke;

/**
 * Used as a super class.
 * <br/> Gives POJO (or a class with setters) the capability of autowiring.
 * <br/> Simply <b>new</b> a class, then all setters would be automatically called
 * <br/> with corresponding parameters.
 * @author wkgcass
 *
 */
public abstract class AutoWire {
	public static boolean DEBUG_MODE=false;
	protected AutoWire(){
		
		//the method would detect whether 'this' is singleton.
		IOCController.registerSingleton(this);
		
		//setters
		Method[] methods = this.getClass().getMethods();
		for(Method m : methods){
			//is setter
			if(m.getName().startsWith("set") && m.getName().charAt(3)>='A' && m.getName().charAt(3)<='Z'
				&& m.getParameterTypes().length==1 &&m.getReturnType()==Void.TYPE
				){
				IOCController.invokeSetter(this, m);
				continue;
			}
			
			//set to automatically invoke
			if(m.isAnnotationPresent(Invoke.class)){
				IOCController.invokeMethod(m, this);
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<?> cls){
		return (T)IOCController.get(cls);
	}
	

}
