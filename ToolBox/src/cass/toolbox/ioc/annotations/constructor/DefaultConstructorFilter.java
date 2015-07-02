package cass.toolbox.ioc.annotations.constructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Set;

import cass.toolbox.ioc.AnnotationHandlingException;
import cass.toolbox.ioc.AutoWire;

/**
 * Priority 0.0
 * <br/>
 * Default implementation of ConstructorFilter
 *  <br/>If only one constructor exists , return the constructor.
 * <br/>If more than one constructor exists,
 * <br/>____if contains a constructor with no parameters, return this constructor.
 * <br/>____else throw exception.
 * @author wkgcass
 *
 */
public class DefaultConstructorFilter implements ConstructorFilter {

	@Override
	public boolean canHandle(Set<Annotation> annotations) {
		return true;
	}

	@Override
	public Constructor<?> handle(Constructor<?>[] cons,
			ConstructorFilterChain chain) throws AnnotationHandlingException {
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    "+Arrays.toString(cons));
		}
		Constructor<?> constr=null;
		try{
			constr=chain.next().handle(cons, chain);
		}catch(AnnotationHandlingException e){}
		if(null!=constr) return constr;
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+" start");
		}
		if(cons.length==1) return cons[0];
		if(cons.length==0) return null;
		for(Constructor<?> con:cons){
			if(con.getParameterTypes().length==0) return con;
		}
		throw new AnnotationHandlingException("Constructor choices are ambiguous");
	}

	@Override
	public double priority() {
		return 0.0;
	}

}
