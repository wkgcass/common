package net.cassite.pure.ioc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to determine the default constructor, <b>or</b> the class
 * redirected to.<br>
 * e.g.<br>
 * You have two constructors, both with parameters, it's ambiguous to determine
 * which one to invoke.<br>
 * Use Default annotation to set a default one. <br>
 * <br>
 * e.g.<br>
 * You have an interface or abstract class, you want to construct an
 * implementation, and there're a lot of work to do if you add Use annotation to
 * each of setters. You can use Default on the interface or abstract class. The
 * system would <b>redirect</b> the constructing target to the one you chose.
 * 
 * @author wkgcass
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.CONSTRUCTOR })
public @interface Default {
        @SuppressWarnings("rawtypes")
        public Class clazz() default Default.class;
}
