package cass.pure.ioc.handlers.param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Member;

import cass.pure.ioc.AnnotationHandlingException;
import cass.pure.ioc.AutoWire;
import cass.pure.ioc.handlers.ParamAnnotationHandler;
import cass.pure.ioc.handlers.ParamHandlerChain;
import cass.pure.ioc.handlers.TempAnnotationHandlingException;

/**
 * Priority 2.0
 * <br/>
 * Handles primitives and arrays.
 * @author wkgcass
 *
 */
public class PrimitiveParameterHandler implements ParamAnnotationHandler {

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
		
		if(cls.isPrimitive()){
			//primitive
			if(cls==boolean.class){
				return (new Boolean(false));
			}
			if(cls==int.class){
				return (new Integer(0));
			}
			if(cls==short.class){
				return (new Short((short)0));
			}
			if(cls==long.class){
				return (new Long(0));
			}
			if(cls==byte.class){
				return (new Byte((byte)0));
			}
			if(cls==double.class){
				return (new Double(0));
			}
			if(cls==float.class){
				return (new Float(0));
			}
			return (new Character((char)0));
		}else if(cls.isArray()){
			if(cls.getComponentType().isPrimitive()){
				//not primitive & is array & component is primitive
				Class<?> clscmp=cls.getComponentType();
				if(clscmp==boolean.class){
					return (new boolean[0]);
				}
				if(clscmp==int.class){
					return (new int[0]);
				}
				if(clscmp==short.class){
					return (new short[0]);
				}
				if(clscmp==long.class){
					return (new long[0]);
				}
				if(clscmp==byte.class){
					return (new byte[0]);
				}
				if(clscmp==double.class){
					return (new double[0]);
				}
				if(clscmp==float.class){
					return (new float[0]);
				}
				return (new char[0]);
			}
			//not primitive & is array & component is not primitive
			return Array.newInstance(cls.getComponentType(), 0);
		}else{
			throw new TempAnnotationHandlingException();
		}
	}

	@Override
	public double priority() {
		return 2.0;
	}

}
