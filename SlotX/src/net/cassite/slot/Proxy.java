package net.cassite.slot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class Proxy implements MethodInterceptor {
        static class ObjectMethodPack {
                private final Module module;
                private final String pluggable;
                private final String instanceName;
                private final Method method;

                ObjectMethodPack(Module module, String pluggable, String instanceName, Method method) {
                        this.module = module;
                        this.pluggable = pluggable;
                        this.instanceName = instanceName;
                        this.method = method;
                }

                Object invoke(Object[] args) throws Throwable {
                        Object target = module.proxyMap.get(pluggable).get(instanceName).getProxy();
                        return method.invoke(target, args);
                }
        }

        final String pluggable;
        final String instanceName;
        final Class<?> cls;

        private final Object proxy;

        final Map<Method, ObjectMethodPack> out = new HashMap<>();
        final Map<Method, ObjectMethodPack> twoWay = new HashMap<>();

        Proxy(String pluggable, String instanceName, Class<?> cls) {
                this.cls = cls;
                this.pluggable = pluggable;
                this.instanceName = instanceName;

                if (instanceName == null) {
                        proxy = null;
                } else {
                        Enhancer enhancer = new Enhancer();
                        enhancer.setSuperclass(cls);
                        enhancer.setCallback(this);
                        proxy = enhancer.create();
                }
        }

        @Override
        public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {
                try {
                        if (out.containsKey(arg1)) {
                                out.get(arg1).invoke(arg2);
                                return null;
                        } else if (twoWay.containsKey(arg1)) {
                                Object res = twoWay.get(arg1).invoke(arg2);
                                return arg3.invokeSuper(arg0, new Object[] { arg2[0], res });
                        } else {
                                return arg3.invokeSuper(arg0, arg2);
                        }
                } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                }
        }

        public Object getProxy() {
                if (null == proxy) {
                        Enhancer enhancer = new Enhancer();
                        enhancer.setSuperclass(cls);
                        enhancer.setCallback(this);
                        return enhancer.create();
                } else {
                        return proxy;
                }
        }

}
