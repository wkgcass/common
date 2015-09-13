package net.cassite.pure.aop;

import org.apache.log4j.Logger;

public abstract class Weaver {
        private static Logger LOGGER = Logger.getLogger(Weaver.class);

        protected abstract void before(AOPPoint point);

        protected abstract void after(AOPPoint point);

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
