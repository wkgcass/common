package cass.pure.aop.iochandler;

import java.lang.annotation.Annotation;

import cass.pure.aop.AOP;
import cass.pure.aop.AOPController;
import cass.pure.ioc.AnnotationHandlingException;
import cass.pure.ioc.handlers.TypeAnnotationHandler;
import cass.pure.ioc.handlers.TypeHandlerChain;

public class AOPConstructingHandler implements TypeAnnotationHandler {

	@Override
	public boolean canHandle(Annotation[] annotations) {
		for (Annotation ann : annotations) {
			if (ann.annotationType() == AOP.class)
				return true;
		}
		return false;
	}

	@Override
	public Object handle(Class<?> cls, TypeHandlerChain chain)
			throws AnnotationHandlingException {
		Object obj = chain.next().handle(cls, chain);
		if (null == obj)
			return null;
		return AOPController.getProxyObject(obj);
	}

	@Override
	public double priority() {
		return -10.0;
	}

}
