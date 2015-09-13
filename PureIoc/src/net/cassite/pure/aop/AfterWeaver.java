package net.cassite.pure.aop;

public abstract class AfterWeaver extends Weaver {
        @Override
        protected final void before(AOPPoint point) {
        };

        @Override
        protected final void exception(AOPPoint point) throws Throwable {
        };
}
