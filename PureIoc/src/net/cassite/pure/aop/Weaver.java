package net.cassite.pure.aop;

import org.apache.log4j.Logger;

/**
 * Around.<br>
 * extends this class to do aop and implements interfaces which you want to use
 * 'introduction'.
 * 
 * @author wkgcass
 * @since 0.1.1
 *
 */
public abstract class Weaver {
        private static Logger LOGGER = Logger.getLogger(Weaver.class);

        /**
         * before invoking the method
         * 
         * @param point
         */
        protected abstract void before(AOPPoint point);

        /**
         * after the method returned
         * 
         * @param point
         */
        protected abstract void after(AOPPoint point);

        /**
         * after throwing
         * 
         * @param point
         * @throws Throwable
         */
        protected abstract void exception(AOPPoint point) throws Throwable;

        public final void doBefore(AOPPoint point) {
                LOGGER.debug("do [Before] with point " + point);
                before(point);
        }

        public final void doAfter(AOPPoint point) {
                LOGGER.debug("do [After Return] with point " + point);
                after(point);
                LOGGER.debug("[After Return] return value is " + point.returnValue());
        }

        public final void doException(AOPPoint point) throws Throwable {
                LOGGER.debug("do [After Exception] with point " + point);
                exception(point);
                LOGGER.debug("[After Exception] return value is " + point.returnValue());
        }
}
