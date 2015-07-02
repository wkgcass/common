package cass.toolbox.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import cass.toolbox.ioc.annotations.Bean;
import cass.toolbox.ioc.annotations.IsSingleton;

/**
 * Used as a super class.
 * <br/> Gives POJO or a class with setters the capability of autowiring.
 * <br/> Simply <b>new</b> a class, then all setters would be automatically called
 * <br/> with corresponding parameters.
 * @author wkgcass
 *
 */
public abstract class AutoWire {
	public static boolean DEBUG_MODE = false;
	
	public AutoWire(){
		//check this class annotation ( IsSingleton Bean )
		Annotation[] anns=this.getClass().getAnnotations();
		
		boolean block=true;
		
		for(Annotation ann : anns){
			if(ann.annotationType()==IsSingleton.class){
				if( ! IOCController.isConstructing(this.getClass())){
					throw new IOCException("Cannot instantiate singleton class :"+this.getClass());
				}else{
					block=false;
				}
			}
			if(ann.annotationType()==Bean.class){
				Bean bean=(Bean)ann;
				if(bean.isSingleton()){
					if(! IOCController.isConstructing(this.getClass())){
						throw new IOCException("Cannot instantiate singleton bean :"+this.getClass());
					}else{
						block=false;
					}
				}
			}
		}
		
		//block
		if(block){
			IOCController.block(this.getClass());
		}
		
		//start constructing
		IOCController.setConstructing(this.getClass());
		
		//setters
		Method[] methods = this.getClass().getMethods();
		for(Method m : methods){
			//is setter
			if(m.getName().startsWith("set") && m.getName().charAt(3)>='A' && m.getName().charAt(3)<='Z'
				&& m.getParameterTypes().length==1 &&m.getReturnType()==Void.TYPE
				){
				IOCController.invokeSetter(this, m);
				
			}
		}
		
		//constructing finished
		IOCController.removeConstructing(this.getClass());
	}
	
	

}
