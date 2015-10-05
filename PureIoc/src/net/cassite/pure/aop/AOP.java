package net.cassite.pure.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables AOP feature of the class
 * 
 * @author wkgcass
 * @since 0.1.1
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AOP {
        /**
         * 
         * @return net.cassite.pure.aop.Weaver
         */
        @SuppressWarnings("rawtypes")
        Class[]value();

        boolean useCglib() default false;
}
