package cass.pure.ioc.handlers.setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;



import cass.pure.ioc.AnnotationHandlingException;
import cass.pure.ioc.AutoWire;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.annotations.Force;
import cass.pure.ioc.handlers.SetterAnnotationHandler;
import cass.pure.ioc.handlers.SetterHandlerChain;
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
		String value=force.value();
		
		Object[] pv=new Object[]{Utility_.stringToObject(value)};
		
		try{
			setter.invoke(target, pv);
		}catch(Exception e){
			if(e instanceof InvocationTargetException){
				Throwable t=((InvocationTargetException)e).getTargetException();
				if(t instanceof RuntimeException) throw (RuntimeException)t;
				if(t instanceof Error) throw (Error)t;
			}
			return false;
		}
		return true;
	}

	@Override
	public double priority() {
		return 8.0;
	}

}
