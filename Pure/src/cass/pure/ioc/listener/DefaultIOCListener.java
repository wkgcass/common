package cass.pure.ioc.listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class DefaultIOCListener implements IOCListener {

	@Override
	public void listenConstruct(Constructor<?> con, Object[] parameterValues) {}

	@Override
	public void listenConstructBean(String beanname) {}

	@Override
	public void listenConstructObject(Class<?> cls) {}

	@Override
	public void listenGet(Class<?> cls) {}

	@Override
	public void listenGetBean(String beanname) {}

	@Override
	public void listenGetObject(Class<?> cls) {}

	@Override
	public void listenInvokeSetter(Object target, Method m) {}

}
