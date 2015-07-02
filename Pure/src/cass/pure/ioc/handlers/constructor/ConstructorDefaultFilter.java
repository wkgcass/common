package cass.pure.ioc.handlers.constructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Set;

import cass.pure.ioc.AnnotationHandlingException;
import cass.pure.ioc.AutoWire;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.annotations.Default;
import cass.pure.ioc.handlers.ConstructorFilter;
import cass.pure.ioc.handlers.ConstructorFilterChain;
/**
 * Priority 10.0
 * <br/>
 * Constructor Filter handling Default annotation
 *	<br/>return the constructor with corresponding Default annotation.
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.Default
 *
 */
public class ConstructorDefaultFilter extends IOCController implements ConstructorFilter {

	@Override
	public boolean canHandle(Set<Annotation> annotations) {
		for(Annotation ann:annotations){
			if(ann.annotationType()==Default.class){
				return true;
			}
		}
		return false;
	}

	@Override
	public Constructor<?> handle(Constructor<?>[] cons,
			ConstructorFilterChain chain) throws AnnotationHandlingException {
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+"    "+Arrays.toString(cons));
		}
		Constructor<?> con=chain.next().handle(cons, chain);
		if(null!=con) return con;
		
		if(AutoWire.DEBUG_MODE){
		System.out.println(this.getClass().getSimpleName()+" start");
		}
		for(Constructor<?> c:cons){
			Default def=c.getAnnotation(Default.class);
			if(def!=null){
				return con;
			}
		}
		return null;
	}

	@Override
	public double priority() {
		return 10.0;
	}

}
