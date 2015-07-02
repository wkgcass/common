package cass.pure.ioc.handlers.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;



import cass.pure.ioc.AnnotationHandlingException;
import cass.pure.ioc.AutoWire;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.annotations.Force;
import cass.pure.ioc.handlers.ParamAnnotationHandler;
import cass.pure.ioc.handlers.ParamHandlerChain;
import cass.toolbox.util.Utility_;

/**
 * Priority 8.0
 * <br/>
 * Handler for Force annotation.
 * <br/> forces a value to be what the string represents.
 * <br/>e.g. "null","nulptr","" represents null, numbers represents integer, 
 * <br/>number with dot in it represents double.
 * @author wkgcass
 *
 * @see cass.toolbox.ioc.annotations.Force
 * @see cass.toolbox.util.Utility_#stringToObject(Object)
 */
public class ParamForceHandler extends IOCController implements
		ParamAnnotationHandler {

	@Override
	public boolean canHandle(Annotation[] annotations) {
		for(Annotation ann:annotations){
			if(ann.annotationType()==Force.class){
				return true;
			}
		}
		return false;
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

		Force force=null;
		for(Annotation ann:toHandle){
			if(ann.annotationType()==Force.class){
				force=(Force)ann;
			}
		}
		String value=force.value();
		
		return Utility_.stringToObject(value);
	}

	@Override
	public double priority() {
		return 8.0;
	}

}
