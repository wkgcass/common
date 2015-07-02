package cass.pure.ioc.handlers.type;

import java.lang.annotation.Annotation;

import cass.pure.ioc.AnnotationHandlingException;
import cass.pure.ioc.AutoWire;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.annotations.Default;
import cass.pure.ioc.handlers.TypeAnnotationHandler;
import cass.pure.ioc.handlers.TypeHandlerChain;

/**
 * Priority 10.0
 * <br/>
 * Handler for Default annotation.
 * <br/>Instantiate the class that 'Default' annotation represents.
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.Default
 *
 */
public class TypeDefaultHandler extends IOCController implements
		TypeAnnotationHandler {

	@Override
	public boolean canHandle(Annotation[] annotations) {
		for(Annotation ann:annotations){
			if(ann.annotationType()==Default.class){
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
		Default ann=cls.getAnnotation(Default.class);

		Class<?> clazz=ann.clazz();
		
		return get(clazz);
	}

	@Override
	public double priority() {
		return 10.0;
	}

}
