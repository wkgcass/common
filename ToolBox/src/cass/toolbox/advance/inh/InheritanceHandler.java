package cass.toolbox.advance.inh;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

import cass.toolbox.util.Arrays_;
/**
 * 
 * @author wkgcass
 *
 */
public class InheritanceHandler extends Extends implements InvocationHandler {
	
	private InheritanceHandler(Class<MultipleInheritable> mi){
		super(mi);
		this.con();
	}
	private InheritanceHandler(Class<?> mi,Object...parameterValues){
		super(mi);
		this.con(parameterValues);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if(method.getName().equals("sup")){
			return this.sup((String)args[0]);
		}
		if(args==null){
			return this.invoke(method.getName());
		}
		return this.invoke(method.getName(), args);
	}
	protected Object field(Object[] args){
		return this.field(args[0].toString());
	}
	public static <T> T generate(Class<?> interf,Class<?> micls){
		return InheritanceHandler.generate(interf,micls, new Object[0]);
	}
	@SuppressWarnings("unchecked")
	public static <T> T generate(Class<?>interf,Class<?> micls, Object...parameterValues){
		if(!MultipleInheritable.class.isAssignableFrom(micls)){
			throw new InheritanceRuntimeException(micls+" is not multipleInheritable.");
		}
		InheritanceHandler ih=new InheritanceHandler(micls,parameterValues);
		Set<Class<?>> set=ih.getInterfaces();
		set.add(interf);
		return (T)Proxy.newProxyInstance(micls.getClassLoader(), Arrays_.castToClass(set.toArray()), ih);
	}
}
