package net.cassite.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.style.reflect.ConstructorSup;

/**
 * choose a constructor
 * 
 * @author wkgcass
 *
 */
public interface ConstructorFilter {
        /**
         * one of given annotations can be handled
         * 
         * @param annotations
         *                a summary of annotations
         * @return true if one of the annos can be handled, false otherwise.
         */
        boolean canHandle(Set<Annotation> annotations);

        /**
         * choose a Constructor to invoke
         * 
         * @param cons
         *                constructors to choose from
         * @param chain
         *                the Constructor chain<br>
         *                usually invoke
         *                <code>chain.next().handle(cons, chain)</code> and
         *                check return value before do real handling.<br>
         *                If the <code>next()</code> handler has a result of
         *                <b>not null</b>, usually the handler simply return
         *                result.
         * @return the chosen constructor
         * @throws AnnotationHandlingException
         */
        ConstructorSup<Object> handle(List<ConstructorSup<Object>> cons, ConstructorFilterChain chain) throws AnnotationHandlingException;
}
