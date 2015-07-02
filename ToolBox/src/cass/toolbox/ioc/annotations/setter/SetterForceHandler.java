package cass.toolbox.ioc.annotations.setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import cass.toolbox.ioc.AnnotationHandlingException;
import cass.toolbox.ioc.AutoWire;
import cass.toolbox.ioc.IOCController;
import cass.toolbox.ioc.annotations.Force;
import cass.toolbox.util.Arrays_;
import cass.toolbox.util.Utility_;

/**
 * Priority 8.0
 * <br/>
 * Handler for Force annotation
 * <br/>invoke the setter with what force.value represents.
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.Force
 * @see cass.toolbox.util.Utility_#stringToObject(Object)
 *
 */
public class SetterForceHandler extends IOCController implements
		SetterAnnotationHandler {

	@Override
	public boolean canHandle(Set<Annotation> annotations) {
		for(Annotation ann:annotations){
			if(ann.annotationType()==Force.class){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean handle(Object target, Method setter, Set<Annotation> toHandle,
			SetterHandlerChain chain) throws AnnotationHandlingException {
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    "+setter);
		}
		if(chain.next().handle(target, setter, toHandle, chain)){
			return true;
		}
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
		String beanName=nameOfConstructingBean(setter.getDeclaringClass());
		//illegal parameters
		if(value.length==0 //no values
				||
				(in.length>value.length)
				||
				(Arrays_.contains(in, "") ? in.length<value.length : in.length<value.length-1)
				) return false;
		
		String o=null;
		int index=Arrays_.indexOf(in, beanName);
		if(-1==index){
			//cannot find corresponding value
			int defaultIndex=Arrays_.indexOf(in, "");
			if(-1==defaultIndex){
				try{
				o=value[in.length];
				}catch(IndexOutOfBoundsException e){
					return false;
				}
			}else{
				//cannot find corresponding value && contains ""(default)
				o=value[defaultIndex];
			}
		}else{
			//found
			o=value[index];
		}
		
		Object[] pv=new Object[]{Utility_.stringToObject(o)};
		
		try{
			setter.invoke(target, pv);
		}catch(Exception e){
			return false;
		}
		return true;
	}

	@Override
	public double priority() {
		return 8.0;
	}

}
