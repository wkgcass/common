package net.cassite.pure.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

public class Handler implements InvocationHandler {

        private final Weaver[] weavers;
        private final Object target;

        public Handler(Weaver[] weavers, Object target) {
                this.weavers = weavers;
                this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                AOPPoint p = new AOPPoint(target, method, args);
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
                Set<Class<?>> interfaces = new HashSet<>();
                for (Class<?> c : target.getClass().getInterfaces()) {
                        interfaces.add(c);
                }
                for (Weaver w : weavers) {
                        for (Class<?> c : w.getClass().getInterfaces()) {
                                interfaces.add(c);
                        }
                }
                return Proxy.newProxyInstance(target.getClass().getClassLoader(), interfaces.toArray(new Class[interfaces.size()]), this);
        }

}
