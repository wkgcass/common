package cass.pure.aop;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;

public class AOPController {
	protected AOPController() {
	}

	private static Set<AOPHandler> handlerSet = new HashSet<AOPHandler>();

	public static void register(AOPHandler handler) {
		handlerSet.add(handler);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getProxyObject(T t) {
		ProxyHandler ph = new ProxyHandler();
		Enhancer en = new Enhancer();
		Class<?> cls = t.getClass();
		while (cls.getSimpleName().contains("$")) {
			cls = cls.getSuperclass();
		}
		if (cls == Object.class) {
			cls = t.getClass();
		}
		en.setSuperclass(cls);
		en.setCallback(ph);
		try {
			return (T) en.create();
		} catch (Exception e) {
			e.printStackTrace();
			throw (RuntimeException) e;
		}
	}

	protected static Object[] doBefore(Object target, Method method,
			Object[] args) throws Throwable {
		AOPBeforeChain chain = new AOPBeforeChain(handlerSet, method);
		return chain.next().before(target, method, args, chain);
	}

	protected static Object doAfter(Object target, Method method,
			Object[] args, Object returnValue) throws Throwable {
		AOPAfterChain chain = new AOPAfterChain(handlerSet, method);
		return chain.next().after(target, method, args, returnValue, chain);
	}
}
