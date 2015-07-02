package cass.toolbox.ioc.annotations.type;

import java.lang.annotation.Annotation;

import cass.toolbox.ioc.AnnotationHandlingException;
import cass.toolbox.ioc.AutoWire;
import cass.toolbox.ioc.IOCController;
import cass.toolbox.ioc.annotations.IsSingleton;

/**
 * Priority 8.0
 * <br/>
 * Handler for IsSingleton annotation.
 * <br/>the class would be considered as a singleton.
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.IsSingleton
 *
 */
public class TypeIsSingletonHandler extends IOCController implements
		TypeAnnotationHandler {

	@Override
	public boolean canHandle(Annotation[] annotations) {
		for(Annotation ann:annotations){
			if(ann.annotationType()==IsSingleton.class){
				return true;
			}
		}
		return false;
	}

	@Override
	public Object handle(Class<?> cls,
			TypeHandlerChain chain) throws AnnotationHandlingException {
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    "+cls);
		}
		try{
			return chain.next().handle(cls, chain);
		}catch(AnnotationHandlingException e){}

		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    start");
		}
		return getObject(cls);
	}

	@Override
	public double priority() {
		return 8.0;
	}

}
