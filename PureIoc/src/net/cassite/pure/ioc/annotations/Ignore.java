package net.cassite.pure.ioc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All setters will be invoked, but you can add Ignore to the setters you don't
 * want the system to invoke.
 * 
 * @author wkgcass
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Ignore {
}
