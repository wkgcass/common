package cass.toolbox.ioc.annotations.setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import cass.toolbox.ioc.AnnotationHandlingException;
import cass.toolbox.ioc.AutoWire;
import cass.toolbox.ioc.IOCController;
import cass.toolbox.ioc.annotations.Use;
import cass.toolbox.util.Arrays_;
import cass.toolbox.util.Utility_;

/**
 * Priority 6.0
 * <br/>
 * Handler for Use annotation.
 * <br/>
 * Use object of the type that the annotation designated.
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.Use
 *
 */
public class SetterUseHandler extends IOCController implements
		SetterAnnotationHandler {

	@Override
	public boolean canHandle(Set<Annotation> annotations) {
		for(Annotation ann:annotations){
			if(ann.annotationType()==Use.class){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean handle(Object target, Method setter,
			Set<Annotation> toHandle, SetterHandlerChain chain)
			throws AnnotationHandlingException {
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    "+setter);
		}
		if(chain.next().handle(target, setter, toHandle, chain)){
			return true;
		}
		
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    start");
		}
		Use use=null;
		for(Annotation ann:toHandle){
			if(ann.annotationType()==Use.class){
				use=(Use)ann;
			}
		}
		String[] bean=use.bean();
		Class<?>[] clazz=use.clazz();
		String[] in=use.in();
		
		String constructingBean=nameOfConstructingBean(target.getClass());
		int index=Arrays_.indexOf(in, constructingBean);
		
		Object obj=null;
		
		try{
		
			if(-1==index){
				//not contain
				int defaultIndex=Arrays_.indexOf(in, "");
				if(defaultIndex==-1){
					//no ""
					obj=get(bean,clazz,in.length+1);
				}else{
					//contains ""
					obj=get(bean,clazz,defaultIndex);
				}
			}else{
				//contains
				obj=get(bean,clazz,index);
			}
		
		}catch(RuntimeException e){
			return false;
		}
		
		Object[] pv=new Object[]{Utility_.stringToObject(obj)};
		try{
			setter.invoke(target, pv);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	private Object get(String[] bean, Class<?>[] clazz, int index){
		Object obj=null;
		try{
			if(bean[index].equals("")){
				//not bean
				obj=getC(clazz,index);
			}else{
				//is bean
				obj=getBean(bean[index]);
			}
		}catch(IndexOutOfBoundsException e){
			//not bean
			obj=getC(clazz,index);
		}
		return obj;
	}
	
	private Object getC(Class<?>[] clazz, int index){
		Object obj=null;
		if(clazz[index].equals(Use.class)){
			//not clazz
			throw new RuntimeException();
		}else{
			obj=get(clazz[index]);
		}
		return obj;
	}

	@Override
	public double priority() {
		return 6.0;
	}

}
