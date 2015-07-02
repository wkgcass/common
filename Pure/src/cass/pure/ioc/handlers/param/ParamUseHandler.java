package cass.pure.ioc.handlers.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

import cass.pure.ioc.AnnotationHandlingException;
import cass.pure.ioc.AutoWire;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.annotations.Use;
import cass.pure.ioc.handlers.ParamAnnotationHandler;
import cass.pure.ioc.handlers.ParamHandlerChain;
import cass.pure.ioc.handlers.TempAnnotationHandlingException;

/**
 * Priority 6.0
 * <br/>
 * Handler for Use annotation.
 * <br/> returns the instance of the class "use" annotation represents.
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.Use
 *
 */
public class ParamUseHandler extends IOCController implements
		ParamAnnotationHandler {

	@Override
	public boolean canHandle(Annotation[] annotations) {
		for(Annotation ann:annotations){
			if(ann.annotationType()==Use.class){
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
		Use use=null;
		for(Annotation ann:toHandle){
			if(ann.annotationType()==Use.class){
				use=(Use)ann;
			}
		}
		
		Class<?> clazz=use.clazz();
		if(clazz!=Use.class) return get(clazz);
		String constant=use.constant();
		if(!constant.equals("")) return retrieveConstant(constant);
		String variable=use.variable();
		if(!variable.equals("")) return retrieveVariable(variable);
		
		throw new TempAnnotationHandlingException();
	}

	@Override
	public double priority() {
		return 6.0;
	}

}
