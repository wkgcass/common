package test;

import java.lang.reflect.Method;

import cass.pure.aop.AOPAfterChain;
import cass.pure.aop.AOPBeforeChain;
import cass.pure.aop.AOPHandler;

public class TestAOPHandler implements AOPHandler {

	@Override
	public boolean canHandle(Method method) {
		String methodName = method.getName();
		if (methodName.equals("toString") || methodName.equals("hashCode"))
			return false;
		return true;
	}

	@Override
	public Object[] before(Object target, Method method, Object[] args,
			AOPBeforeChain chain) throws Throwable {
		System.out.println("Proxy����ִ��Before");
		return chain.next().before(target, method, args, chain);
	}

	@Override
	public Object after(Object target, Method method, Object[] args,
			Object returnValue, AOPAfterChain chain) throws Throwable {
		System.out.println("Proxy����ִ��After");
		return chain.next().after(target, method, args, returnValue, chain);
	}

	@Override
	public double layer() {
		return 0;
	}

}
