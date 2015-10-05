package net.cassite.pure.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibHandler implements MethodInterceptor {

        private final Weaver[] weavers;
        private final Object target;

        CglibHandler(Weaver[] weavers, Object target) {
                this.weavers = weavers;
                this.target = target;
        }

        @Override
        public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {
                AOPPoint p = new AOPPoint(target, arg1, arg2);
                int tmpI = weavers.length - 1;
                for (int i = 0; i < weavers.length; ++i) {
                        weavers[i].doBefore(p);
                        if (p.doReturn) {
                                tmpI = i;
                                break;
                        }
                }
                if (!p.doReturn) {
                        try {
                                if (p.method.getDeclaringClass().isInstance(p.target)) {
                                        p.returnValue(p.method.invoke(p.target, p.args));
                                } else {
                                        for (Weaver w : weavers) {
                                                if (p.method.getDeclaringClass().isInstance(w)) {
                                                        p.returnValue(p.method.invoke(w, p.args));
                                                        break;
                                                }
                                        }
                                }
                        } catch (Throwable t) {
                                if (t instanceof InvocationTargetException) {
                                        t = ((InvocationTargetException) t).getTargetException();
                                }
                                p.setThrowable(t);
                                for (int i = weavers.length - 1; i >= 0; --i) {
                                        weavers[i].doException(p);
                                }
                                return p.returnValue();
                        }
                }
                for (int i = tmpI; i >= 0; --i) {
                        weavers[i].doAfter(p);
                }
                return p.returnValue();
        }

        public Object proxy() {
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(target.getClass());

                Set<Class<?>> interfaces = new HashSet<>();
                for (Class<?> c : target.getClass().getInterfaces()) {
                        interfaces.add(c);
                }
                for (Weaver w : weavers) {
                        for (Class<?> c : w.getClass().getInterfaces()) {
                                interfaces.add(c);
                        }
                }

                enhancer.setInterfaces(interfaces.toArray(new Class[interfaces.size()]));
                enhancer.setCallback(this);
                return enhancer.create();
        }

}
