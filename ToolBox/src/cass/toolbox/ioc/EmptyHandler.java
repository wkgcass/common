package cass.toolbox.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import cass.toolbox.ioc.annotations.constructor.ConstructorFilter;
import cass.toolbox.ioc.annotations.constructor.ConstructorFilterChain;
import cass.toolbox.ioc.annotations.param.ParamAnnotationHandler;
import cass.toolbox.ioc.annotations.param.ParamHandlerChain;
import cass.toolbox.ioc.annotations.param.TempAnnotationHandlingException;
import cass.toolbox.ioc.annotations.setter.SetterAnnotationHandler;
import cass.toolbox.ioc.annotations.setter.SetterHandlerChain;
import cass.toolbox.ioc.annotations.type.TypeAnnotationHandler;
import cass.toolbox.ioc.annotations.type.TypeHandlerChain;

/**
 * Handles every kind of annotations.
 * <br/>false/null/exception would be returned/thrown in order to end the HandlingChain.
 * <br/>priority Double.MAX_VALUE
 * @author wkgcass
 *
 */
public class EmptyHandler implements SetterAnnotationHandler, ParamAnnotationHandler, TypeAnnotationHandler, ConstructorFilter {
	
	private static EmptyHandler inst=null;
	
	private EmptyHandler(){}
	
	public static EmptyHandler getInstance(){
		if(null==inst){
			synchronized(EmptyHandler.class){
				if(null==inst){
					inst=new EmptyHandler();
				}
			}
		}
		return inst;
	}

	@Override
	public boolean canHandle(Set<Annotation> annotations) {
		return true;
	}

	@Override
	public boolean handle(Object target, Method setter, Set<Annotation> toHandle,
			SetterHandlerChain chain) throws AnnotationHandlingException {
		if(AutoWire.DEBUG_MODE){
		System.out.println("EmptyHandler.handle (setter)"+setter);
		}
		return false;
	}

	@Override
	public double priority() {
		return Double.MAX_VALUE;
	}

	@Override
	public Object handle(Member caller, Class<?> cls, Annotation[] toHandle,
			ParamHandlerChain chain) throws AnnotationHandlingException {
		if(AutoWire.DEBUG_MODE){
		System.out.println("EmptyHandler.handle (param)"+cls);
		}
		throw new TempAnnotationHandlingException();
	}

	@Override
	public boolean canHandle(Annotation[] annotations) {
		return true;
	}

	@Override
	public Object handle(Class<?> cls, TypeHandlerChain chain)
			throws AnnotationHandlingException {
		if(AutoWire.DEBUG_MODE){
		System.out.println("EmptyHandler.handle (type)"+cls);
		}
		throw new TempAnnotationHandlingException();
	}

	@Override
	public Constructor<?> handle(Constructor<?>[] cons,
			ConstructorFilterChain chain) throws AnnotationHandlingException {
		if(AutoWire.DEBUG_MODE){
		System.out.println("EmptyHandler.handle (cons)"+Arrays.toString(cons));
		}
		return null;
	}

}
