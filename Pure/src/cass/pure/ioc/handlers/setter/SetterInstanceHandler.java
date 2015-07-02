package cass.pure.ioc.handlers.setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import cass.pure.ioc.AnnotationHandlingException;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.handlers.SetterAnnotationHandler;
import cass.pure.ioc.handlers.SetterHandlerChain;

public class SetterInstanceHandler extends IOCController implements
		SetterAnnotationHandler {

	@Override
	public boolean canHandle(Set<Annotation> annotations) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handle(Object target, Method setter,
			Set<Annotation> toHandle, SetterHandlerChain chain)
			throws AnnotationHandlingException {
		if(chain.next().handle(target, setter, toHandle, chain)){
			return true;
		}
		try{
			setter.invoke(target, get(setter.getParameterTypes()[0]));
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
		return 4.0;
	}

}
