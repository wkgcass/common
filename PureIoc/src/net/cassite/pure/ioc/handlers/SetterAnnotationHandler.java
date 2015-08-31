package net.cassite.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.util.Set;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.style.reflect.MethodSupport;

/**
 * invoke a setter
 * 
 * @author wkgcass
 *
 */
public interface SetterAnnotationHandler {
        /**
         * one of given annotations can be handled
         * 
         * @param annotations
         *                a summary of annotations
         * @return true if one of the annos can be handled, false otherwise.
         */
        boolean canHandle(Set<Annotation> annotations);

        /**
         * invoke a setter
         * 
         * @param target
         *                object to invoke on
         * @param setter
         *                setter to invoke
         * @param toHandle
         *                annotations presented on setter/setter parameter/field
         * @param chain
         *                the Setter Chain<br>
         *                Usually call
         *                <code>chain.next().handle(target, setter, toHandle, chain)</code>
         *                before do real handling<br>
         *                It is recommended that if <code>next()</code> handler
         *                returned false, which means it failed invoking the
         *                setter, do current handling. else, simply return the
         *                true value.
         * @return boolean true if successfully invoked the setter, false
         *         otherwise.
         * @throws AnnotationHandlingException
         */
        boolean handle(Object target, MethodSupport<Object, Object> setter, Set<Annotation> toHandle, SetterHandlerChain chain)
                        throws AnnotationHandlingException;
}
