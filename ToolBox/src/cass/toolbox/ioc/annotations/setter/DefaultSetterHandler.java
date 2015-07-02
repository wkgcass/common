package cass.toolbox.ioc.annotations.setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import cass.toolbox.ioc.AnnotationHandlingException;
import cass.toolbox.ioc.AutoWire;
import cass.toolbox.ioc.IOCController;

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
			setter.invoke(target, get(setter.getParameterTypes()[0]));
		}catch(Exception e){
			throw new AnnotationHandlingException(e);
		}
		return true;
	}

	@Override
	public double priority() {
		return 0.0;
	}

}
