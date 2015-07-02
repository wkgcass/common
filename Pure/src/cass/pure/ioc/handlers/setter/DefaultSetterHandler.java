package cass.pure.ioc.handlers.setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import cass.pure.ioc.AnnotationHandlingException;
import cass.pure.ioc.AutoWire;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.handlers.SetterAnnotationHandler;
import cass.pure.ioc.handlers.SetterHandlerChain;

/**
 * Priority 0.0
 * <br/>
 * Default implementation of SetterAnnotationHandler
 * <br/>simply invoke the method with object of method's parameter type.
 * @author wkgcass
 *
 */
public class DefaultSetterHandler extends IOCController implements
		SetterAnnotationHandler {

	@Override
	public boolean canHandle(Set<Annotation> annotations) {
		return true;
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
		try{
			Class<?> pt=setter.getParameterTypes()[0];
			Object pv=null;
			if(pt.getName().startsWith("java.") || pt.getName().startsWith("javax.")){
				pv=get(pt);
			}else{
				pv=retrieveConstant(pt);
				if(null==pv) pv=get(pt);
			}
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
		return 0.0;
	}

}
