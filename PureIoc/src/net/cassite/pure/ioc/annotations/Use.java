package net.cassite.pure.ioc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Force a setter or one of method's parameter to use the given class's instance
 * / constant / variable.<br>
 * It's similar to Force but it is not limited to primitives or Strings.<br>
 * Constants and variables can be registered in IOCController
 * 
 * @author wkgcass
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface Use {
        @SuppressWarnings("rawtypes")
        public Class clazz() default Use.class;

        public String constant() default "";

        public String variable() default "";
}
