package cass.pure.ioc.handlers.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import cass.pure.ioc.AnnotationHandlingException;
import cass.pure.ioc.AutoWire;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.annotations.Wire;
import cass.pure.ioc.handlers.TypeAnnotationHandler;
import cass.pure.ioc.handlers.TypeHandlerChain;

/**
 * Priority -2.0
 * <br/>
 * Handler for Wire annotation.
 * <br/>if the class extends from AutoWire, this would simply return
 * <br/>else, all setters would be autowired.
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.Wire
 *
 */
public class TypeWireHandler extends IOCController implements TypeAnnotationHandler {

	@Override
	public boolean canHandle(Annotation[] annotations) {
		for(Annotation ann:annotations){
			if(ann.annotationType()==Wire.class){
				return true;
			}
		}
		return false;
	}

	@Override
	public Object handle(Class<?> cls, TypeHandlerChain chain)
			throws AnnotationHandlingException {
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    "+cls);
		}
		Object inst=null;
		try{
			inst=chain.next().handle(cls, chain);
		}catch(AnnotationHandlingException e){}

		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    start");
		}
		if(null==inst) throw new AnnotationHandlingException("Instatiation failed");
		if(AutoWire.class.isAssignableFrom(cls)) return inst;
		/*******************************/
		//setters
		Method[] methods = inst.getClass().getMethods();
		for(Method m : methods){
			//is setter
			if(m.getName().startsWith("set") && m.getName().charAt(3)>='A' && m.getName().charAt(3)<='Z'
				&& m.getParameterTypes().length==1 &&m.getReturnType()==Void.TYPE
				){
				invokeSetter(inst,m);
				
			}
		}
		 /*******************************/

		return inst;
	}

	@Override
	public double priority() {
		return -2.0;
	}

}
