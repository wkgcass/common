package net.cassite.pure.ioc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.cassite.pure.ioc.ExtendingHandler;

/**
 * This annotations aims to simplify retrieving objects from other object
 * containers.<br>
 * e.g. if you need to retrieve an object from spring, you need to (for example)
 * create a <code>Spring</code> annotation, then create
 * <code>SetterSpringHandler</code> and <code>ParamSpringHandler</code> to
 * finish the injection and object-retrieving. <br>
 * However the only extension was retrieving an object from Spring.<br>
 * Now you can use this to simplify your retrieving process.
 * 
 * @author wkgcass
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
public @interface Extend {
        /**
         * ExtendingHandler implement class. The handler would be retrieved
         * using {@link net.cassite.pure.ioc.AutoWire#get(Class)}
         * 
         * @return
         * @see net.cassite.pure.ioc.AutoWire#get(Class)
         */
        Class<ExtendingHandler>handler();

        /**
         * Arguments to fill into your handler
         * 
         * @return
         */
        String[]args();
}
