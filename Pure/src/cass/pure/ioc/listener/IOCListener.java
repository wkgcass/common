package cass.pure.ioc.listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface IOCListener {
	void listenInvokeSetter(Object target, Method m);
	void listenConstruct(Constructor<?> con, Object[] parameterValues);
	void listenConstructBean(String beanname);
	void listenConstructObject(Class<?> cls);
	void listenGet(Class<?> cls);
	void listenGetBean(String beanname);
	void listenGetObject(Class<?> cls);
}
