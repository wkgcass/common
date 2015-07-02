package cass.toolbox.ioc.annotations.constructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Set;

import cass.toolbox.ioc.AnnotationHandlingException;
import cass.toolbox.ioc.AutoWire;
import cass.toolbox.ioc.IOCController;
import cass.toolbox.ioc.annotations.Default;
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
		Default def=null;
		for(Constructor<?> c:cons){
			def=c.getAnnotation(Default.class);
			if(def!=null && (def.bean().equals("") || def.bean().equals(nameOfConstructingBean(c.getDeclaringClass())))){
				con=c;
				break;
			}
		}
		return con;
	}

	@Override
	public double priority() {
		return 10.0;
	}

}
