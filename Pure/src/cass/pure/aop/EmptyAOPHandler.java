package cass.pure.aop;

import java.lang.reflect.Method;

public class EmptyAOPHandler implements AOPHandler {

	@Override
	public boolean canHandle(Method method) {
		return true;
	}

	@Override
	public Object[] before(Object target, Method method, Object[] args,
			AOPBeforeChain chain) throws Throwable {
		return args;
	}

	@Override
	public Object after(Object target, Method method, Object[] args,
			Object returnValue, AOPAfterChain chain) throws Throwable {
		return returnValue;
	}

	@Override
	public double layer() {
		return Double.MAX_VALUE;
	}

}
