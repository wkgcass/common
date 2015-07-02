package cass.pure.aop;

import java.lang.reflect.Method;

public interface AOPHandler {
	boolean canHandle(Method method);

	Object[] before(Object target, Method method, Object[] args,
			AOPBeforeChain chain) throws Throwable;

	Object after(Object target, Method method, Object[] args,
			Object returnValue, AOPAfterChain chain) throws Throwable;

	double layer();
}
