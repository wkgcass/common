package net.cassite.pure.aop;

/**
 * After Return
 * 
 * @author wkgcass
 * @since 0.1.1
 *
 */
public abstract class AfterWeaver extends Weaver {
        @Override
        protected final void before(AOPPoint point) {
        };

        @Override
        protected final void exception(AOPPoint point) throws Throwable {
        };
}
