package cass.pure.ioc.handlers.type;

import java.lang.annotation.Annotation;

import cass.pure.ioc.AnnotationHandlingException;
import cass.pure.ioc.AutoWire;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.handlers.TypeAnnotationHandler;
import cass.pure.ioc.handlers.TypeHandlerChain;

/**
 * Priority 0.0
 * <br/>
 * Default implementation of TypeAnnotationHandler
 * <br/>simply generate the object then return.
 * @author wkgcass
 *
 */
public class DefaultTypeHandler extends IOCController implements
		TypeAnnotationHandler {

	@Override
	public boolean canHandle(Annotation[] annotations) {
		return true;
	}

	@Override
	public Object handle(Class<?> cls, TypeHandlerChain chain)
			throws AnnotationHandlingException {
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    "+cls);
		}
		try{
			return chain.next().handle(cls, chain);
		}catch(AnnotationHandlingException e){}
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    start");
		}
		return constructObject(cls);
	}

	@Override
	public double priority() {
		return 0.0;
	}

}
