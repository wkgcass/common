package net.cassite.pure.ioc.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;

/**
 * Mark a class as Singleton. <br>
 * The system will only have one instance of the class.<br>
 * Note that if the class extends AutoWire, you cannot 'new' the class twice.
 * 
 * @author wkgcass
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Singleton {
}
