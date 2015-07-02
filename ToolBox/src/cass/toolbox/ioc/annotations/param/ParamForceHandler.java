package cass.toolbox.ioc.annotations.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

import cass.toolbox.ioc.AnnotationHandlingException;
import cass.toolbox.ioc.AutoWire;
import cass.toolbox.ioc.IOCController;
import cass.toolbox.ioc.annotations.Force;
import cass.toolbox.util.Arrays_;
import cass.toolbox.util.Utility_;

/**
 * Priority 8.0
 * <br/>
 * Handler for Force annotation.
 * <br/> forces a value to be what the string represents.
 * <br/>e.g. "null","nulptr","" represents null, numbers represents integer, 
 * <br/>number with dot in it represents double.
 * @author wkgcass
 *
 * @see cass.toolbox.ioc.annotations.Force
 * @see cass.toolbox.util.Utility_#stringToObject(Object)
 */
public class ParamForceHandler extends IOCController implements
		ParamAnnotationHandler {

	@Override
	public boolean canHandle(Annotation[] annotations) {
		for(Annotation ann:annotations){
			if(ann.annotationType()==Force.class){
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


		Force force=null;
		for(Annotation ann:toHandle){
			if(ann.annotationType()==Force.class){
				force=(Force)ann;
			}
		}
		String[] value=force.value();
		String[] in=force.in();
		String beanName=nameOfConstructingBean(caller.getDeclaringClass());
		//illegal parameters
		if(value.length==0 //no values
				||
				(in.length>value.length)
				||
				(Arrays_.contains(in, "") ? in.length<value.length : in.length<value.length-1)
				) {
				throw new TempAnnotationHandlingException();
			
		}
		
		String o=null;
		int index=Arrays_.indexOf(in, beanName);
		if(-1==index){
			//cannot find corresponding value
			int defaultIndex=Arrays_.indexOf(in, "");
			if(-1==defaultIndex){
				try{
				o=value[in.length];
				}catch(IndexOutOfBoundsException e){
					throw new TempAnnotationHandlingException();
				}
			}else{
				//cannot find corresponding value && contains ""(default)
				o=value[defaultIndex];
			}
		}else{
			//found
			o=value[index];
		}
		
		return Utility_.stringToObject(o);
	}

	@Override
	public double priority() {
		return 8.0;
	}

}
