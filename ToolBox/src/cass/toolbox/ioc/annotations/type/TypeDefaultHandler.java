package cass.toolbox.ioc.annotations.type;

import java.lang.annotation.Annotation;

import cass.toolbox.ioc.AnnotationHandlingException;
import cass.toolbox.ioc.AutoWire;
import cass.toolbox.ioc.IOCController;
import cass.toolbox.ioc.annotations.Default;

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
		String bean=ann.bean();
		Class<?> clazz=ann.clazz();
		if(bean.equals("") && clazz==Default.class) throw new AnnotationHandlingException("default class type not declared.");
		if(clazz==Default.class){
			return getBean(bean);
		}
		return get(clazz);
	}

	@Override
	public double priority() {
		return 10.0;
	}

}
