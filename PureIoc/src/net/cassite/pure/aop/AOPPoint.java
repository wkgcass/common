package net.cassite.pure.aop;

import java.lang.reflect.Method;
import java.util.Arrays;

public class AOPPoint {
        public final Object target;
        public final Object[] args;
        private final Class<?>[] parameterTypes;
        private Object returnValue = null;
        private Throwable exception = null;
        public final Method method;

        boolean doReturn = false;

        public AOPPoint(Object target, Method method, Object[] args) {
                this.target = target;
                this.method = method;
                this.args = args == null ? new Object[0] : Arrays.copyOf(args, args.length);
                this.parameterTypes = Arrays.copyOf(method.getParameterTypes(), method.getParameterCount());
        }

        public Class<?>[] parameterTypes() {
                return Arrays.copyOf(parameterTypes, parameterTypes.length);
        }

        public void returnValue(Object r) {
                this.doReturn = true;
                this.returnValue = r;
        }

        public Object returnValue() {
                return returnValue;
        }

        void setThrowable(Throwable t) {
                this.exception = t;
        }

        public Throwable exception() {
                return exception;
        }

        public String toString() {
                return "target: " + target + ", method: " + method + ", args: " + Arrays.toString(args) + ", returnValue: " + returnValue
                                + ", exception: " + exception;
        }
}
