package cass.toolbox.ioc.annotations.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

import cass.toolbox.ioc.AnnotationHandlingException;
import cass.toolbox.ioc.AutoWire;
import cass.toolbox.ioc.IOCController;

/**
 * Priority 0.0
 * <br/>
 * Default implementation of ParamAnnotationHandler
 * <br/> simply return the corresponding value of given parameter type.
 * @author wkgcass
 *
 */
public class DefaultParamHandler extends IOCController implements
		ParamAnnotationHandler {

	@Override
	public boolean canHandle(Annotation[] annotations) {
		return true;
	}

	@Override
	public Object handle(Member caller, Class<?> cls, Annotation[] toHandle,
			ParamHandlerChain chain) throws AnnotationHandlingException {
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    "+cls);
		}
		try{
			return chain.next().handle(caller, cls, toHandle, chain);
		}catch(AnnotationHandlingException e){}
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    start");
		}
		return get(cls);
	}

	@Override
	public double priority() {
		return 0.0;
	}

}
