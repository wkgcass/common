package cass.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import cass.pure.ioc.AnnotationHandlingException;
import cass.pure.ioc.AutoWire;
import cass.pure.ioc.handlers.ConstructorFilter;
import cass.pure.ioc.handlers.ConstructorFilterChain;
import cass.pure.ioc.handlers.ParamAnnotationHandler;
import cass.pure.ioc.handlers.ParamHandlerChain;
import cass.pure.ioc.handlers.SetterAnnotationHandler;
import cass.pure.ioc.handlers.SetterHandlerChain;
import cass.pure.ioc.handlers.TempAnnotationHandlingException;
import cass.pure.ioc.handlers.TypeAnnotationHandler;
import cass.pure.ioc.handlers.TypeHandlerChain;

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
