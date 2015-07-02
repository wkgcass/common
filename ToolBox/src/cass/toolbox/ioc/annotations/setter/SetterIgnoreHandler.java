package cass.toolbox.ioc.annotations.setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import cass.toolbox.ioc.AnnotationHandlingException;
import cass.toolbox.ioc.AutoWire;
import cass.toolbox.ioc.annotations.Ignore;

/**
 * Priority 10.0
 * <br/>
 * Handler for Ignore annotation.
 * <br/>the setter would be ignored.
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.Ignore
 *
 */
public class SetterIgnoreHandler implements SetterAnnotationHandler {

	@Override
	public boolean canHandle(Set<Annotation> annotations) {
		for(Annotation ann:annotations){
			if(ann.annotationType()==Ignore.class){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean handle(Object target, Method setter, Set<Annotation> toHandle,
			SetterHandlerChain chain) throws AnnotationHandlingException {
		chain.next().handle(target, setter, toHandle, chain);
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    start");
		}
		return true;
		
	}

	@Override
	public double priority() {
		return 10.0;
	}

}
