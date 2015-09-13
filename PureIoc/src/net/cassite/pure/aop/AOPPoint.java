package net.cassite.pure.aop;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Info about proxy
 * 
 * @author wkgcass
 * @since 0.1.1
 *
 */
public class AOPPoint {
        /**
         * target to invoke methods on
         */
        public final Object target;
        /**
         * arguments to invoke the method, can be changed freely
         */
        public final Object[] args;
        private Object returnValue = null;
        private Throwable exception = null;
        /**
         * method to invoke
         */
        public final Method method;

        boolean doReturn = false;

        AOPPoint(Object target, Method method, Object[] args) {
                this.target = target;
                this.method = method;
                this.args = args == null ? new Object[0] : Arrays.copyOf(args, args.length);
        }

        /**
         * set the return value<br>
         * if it's invoked in 'before', the process would directly jump to
         * 'after return'
         * 
         * @param r
         *                the return value to set
         */
        public void returnValue(Object r) {
                this.doReturn = true;
                this.returnValue = r;
        }

        /**
         * retrieve the return value<br>
         * it will always be null in 'before'
         * 
         * @return return value
         */
        public Object returnValue() {
                return returnValue;
        }

        void setThrowable(Throwable t) {
                this.exception = t;
        }

        /**
         * retrieve the exception occurred
         * 
         * @return occurred exception
         */
        public Throwable exception() {
                return exception;
        }

        public String toString() {
                return "target: " + target + ", method: " + method + ", args: " + Arrays.toString(args) + ", returnValue: " + returnValue
                                + ", exception: " + exception;
        }
}
