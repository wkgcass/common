package net.cassite.pure.aop;

public abstract class BeforeWeaver extends Weaver {
        @Override
        protected final void exception(AOPPoint point) throws Throwable {
        };

        @Override
        protected final void after(AOPPoint point) {
        };
}
