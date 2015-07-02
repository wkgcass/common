package cass.pure.persist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	String name() default "";

	String type() default "";

	String Default() default "cass.purify.persist.no_default_string_value";

	boolean unique() default false;

	boolean autoIncrement() default false;

	boolean notNull() default false;

	boolean primary() default false;
}
