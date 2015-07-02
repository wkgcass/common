package cass.pure.persist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Maps {
	public Class<?> ref();

	public String target();

	public String value();

	public String foreign() default "";
}
