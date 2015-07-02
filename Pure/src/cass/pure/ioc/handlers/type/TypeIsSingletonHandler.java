package cass.pure.ioc.handlers.type;

import java.lang.annotation.Annotation;

import cass.pure.ioc.AnnotationHandlingException;
import cass.pure.ioc.AutoWire;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.annotations.Singleton;
import cass.pure.ioc.handlers.TypeAnnotationHandler;
import cass.pure.ioc.handlers.TypeHandlerChain;

/**
 * Priority 8.0
 * <br/>
 * Handler for IsSingleton annotation.
 * <br/>the class would be considered as a singleton.
 * @author wkgcass
 * 
 * @see cass.toolbox.Singleton.annotations.IsSingleton
 *
 */
public class TypeIsSingletonHandler extends IOCController implements
		TypeAnnotationHandler {

	@Override
	public boolean canHandle(Annotation[] annotations) {
		for(Annotation ann:annotations){
			if(ann.annotationType()==Singleton.class){
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
