package cass.toolbox.ioc.annotations.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

import cass.toolbox.ioc.AnnotationHandlingException;
import cass.toolbox.ioc.AutoWire;
import cass.toolbox.ioc.IOCController;
import cass.toolbox.ioc.annotations.Use;
import cass.toolbox.util.Arrays_;

/**
 * Priority 6.0
 * <br/>
 * Handler for Use annotation.
 * <br/> returns the instance of the class "use" annotation represents.
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.Use
 *
 */
public class ParamUseHandler extends IOCController implements
		ParamAnnotationHandler {

	@Override
	public boolean canHandle(Annotation[] annotations) {
		for(Annotation ann:annotations){
			if(ann.annotationType()==Use.class){
				return true;
			}
		}
		return false;
	}

	@Override
	public Object handle(Member caller, Class<?> cls, Annotation[] toHandle,
			ParamHandlerChain chain) throws AnnotationHandlingException {
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    "+cls);
		}
		try{
			return chain.next().handle(caller, cls, toHandle, chain);
		}catch(AnnotationHandlingException e){}
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
		
		String constructingBean=nameOfConstructingBean(caller.getDeclaringClass());
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
			throw new TempAnnotationHandlingException();
		}
		return obj;
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
