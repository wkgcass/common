package net.cassite.pure.ioc.handlers.constructor;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.annotations.Default;
import net.cassite.pure.ioc.handlers.ConstructorFilter;
import net.cassite.pure.ioc.handlers.ConstructorFilterChain;
import net.cassite.style.aggregation.Aggregation;
import net.cassite.style.reflect.ConstructorSup;

/**
 * Constructor Filter handling Default annotation <br>
 * return the constructor with Default annotation attached.
 * 
 * @author wkgcass
 * 
 * @see Default
 *
 */
public class ConstructorDefaultFilter extends Aggregation implements ConstructorFilter {

        private static final Logger LOGGER = Logger.getLogger(ConstructorDefaultFilter.class);

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
                LOGGER.debug("Entered ConstructorDefaultFilter with args:\n\tcons:\t" + cons + "\n\tchain:\t" + chain);
                ConstructorSup<Object> nextRes = chain.next().handle(cons, chain);
                if (null == nextRes) {
                        LOGGER.debug("start handling with ConstructorDefaultFilter");
                }
                return nextRes == null ? If($(cons).findOne(c -> c.isAnnotationPresent(Default.class)), c -> c).Else(() -> null) : nextRes;
        }

}
