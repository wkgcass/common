package net.cassite.pure.ioc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Wire the instance when the system come across this class<br/>
 * Note that 'new' will not have any effect.<br/>
 * Also you can invoke {@link net.cassite.pure.ioc.AutoWire#wire(Object)} in
 * constructor to get full support of the system.<br/>
 * Do not use both these methods at the same time in one class.
 * 
 * @author wkgcass
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Wire {
}
