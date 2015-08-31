package net.cassite.pure.ioc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Force a setter or one of method's parameter to use the given value.<br>
 * The system will try to transform the value into proper type.<br>
 * Only use this method on those parameters/setters which types are primitive or
 * String
 * 
 * @author wkgcass
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface Force {
        public String value();
}
