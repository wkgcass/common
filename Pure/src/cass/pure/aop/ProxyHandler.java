package cass.pure.aop;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ProxyHandler extends AOPController implements MethodInterceptor {

	@Override
	public Object intercept(Object arg0, Method arg1, Object[] arg2,
			MethodProxy arg3) throws Throwable {
		try {
			arg2 = doBefore(arg0, arg1, arg2);
		} catch (Throwable t) {
			if (t instanceof SealedReturnValue) {
				return ((SealedReturnValue) t).getReturnValue();
			} else {
				throw t;
			}
		}
		Object retVal = arg3.invokeSuper(arg0, arg2);
		return doAfter(arg0, arg1, arg2, retVal);
	}

}
