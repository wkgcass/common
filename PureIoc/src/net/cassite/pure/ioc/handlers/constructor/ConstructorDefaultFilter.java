package net.cassite.pure.ioc.handlers.constructor;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.annotations.Default;
import net.cassite.pure.ioc.handlers.ConstructorFilter;
import net.cassite.pure.ioc.handlers.ConstructorFilterChain;
import net.cassite.style.aggregation.Aggregation;
import net.cassite.style.reflect.ConstructorSup;

/**
 * Constructor Filter handling Default annotation <br/>
 * return the constructor with Default annotation attached.
 * 
 * @author wkgcass
 * 
 * @see Default
 *
 */
public class ConstructorDefaultFilter extends Aggregation implements ConstructorFilter {

        @Override
        public boolean canHandle(Set<Annotation> annotations) {
                for (Annotation ann : annotations) {
                        if (ann.annotationType() == Default.class) {
                                return true;
                        }
                }
                return false;
        }

        @Override
        public ConstructorSup<Object> handle(List<ConstructorSup<Object>> cons, ConstructorFilterChain chain) throws AnnotationHandlingException {
                ConstructorSup<Object> nextRes = chain.next().handle(cons, chain);
                return nextRes == null ? If($(cons).findOne(c -> c.isAnnotationPresent(Default.class)), c -> {
                        return c;
                }).Else(() -> null) : nextRes;
        }

}
