package net.cassite.pure.ioc.handlers.constructor;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.handlers.ConstructorFilter;
import net.cassite.pure.ioc.handlers.ConstructorFilterChain;
import static net.cassite.style.aggregation.Aggregation.*;
import net.cassite.style.reflect.ConstructorSup;

/**
 * <br>
 * Default implementation of ConstructorFilter <br>
 * If only one constructor exists , return the constructor. <br>
 * If more than one constructor exists, <br>
 * ____if contains a constructor with no parameters, return this constructor.
 * <br>
 * ____else throw exception.
 * 
 * @author wkgcass
 *
 */
public class DefaultConstructorFilter implements ConstructorFilter {

        private static final Logger LOGGER = Logger.getLogger(DefaultConstructorFilter.class);

        @Override
        public boolean canHandle(Set<Annotation> annotations) {
                return true;
        }

        @Override
        public ConstructorSup<Object> handle(List<ConstructorSup<Object>> cons, ConstructorFilterChain chain) throws AnnotationHandlingException {
                LOGGER.debug("Entered DefaultConstructorFilter with args:\n\tcons:\t" + cons + "\n\tchain:\t" + chain);
                ConstructorSup<Object> nextCon = null;
                try {
                        nextCon = chain.next().handle(cons, chain);
                } catch (AnnotationHandlingException e) {
                }

                if (nextCon != null) {
                        return nextCon;
                }

                LOGGER.debug("start handling with DefaultConstructorFilter");

                if (cons.size() == 1) {
                        return cons.get(0);
                }
                if (cons.isEmpty()) {
                        return null;
                }
                return If($(cons).findOne(c -> c.argCount() == 0), c -> c).Else(() -> {
                        throw new AnnotationHandlingException("Constructor choices are ambiguous");
                });

        }

}
