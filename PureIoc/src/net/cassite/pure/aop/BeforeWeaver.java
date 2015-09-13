package net.cassite.pure.aop;

/**
 * Before
 * 
 * @author wkgcass
 * @since 0.1.1
 *
 */
public abstract class BeforeWeaver extends Weaver {
        @Override
        protected final void exception(AOPPoint point) throws Throwable {
        };

        @Override
        protected final void after(AOPPoint point) {
        };
}
