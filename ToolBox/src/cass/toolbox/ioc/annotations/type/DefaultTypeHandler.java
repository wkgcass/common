package cass.toolbox.ioc.annotations.type;

import java.lang.annotation.Annotation;

import cass.toolbox.ioc.AnnotationHandlingException;
import cass.toolbox.ioc.AutoWire;
import cass.toolbox.ioc.IOCController;

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
