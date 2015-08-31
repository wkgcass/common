package net.cassite.pure.ioc.handlers;

import java.lang.annotation.Annotation;

import net.cassite.pure.ioc.AnnotationHandlingException;

/**
 * retrieve an instance of given type.
 * 
 * @author wkgcass
 *
 */
public interface TypeAnnotationHandler {
        /**
         * one of given annotations can be handled
         * 
         * @param annotations
         *                a summary of annotations
         * @return true if one of the annos can be handled, false otherwise.
         */
        boolean canHandle(Annotation[] annotations);

        /**
         * retrieve an instance of given type.
         * 
         * @param cls
         *                instance of which to retrieve
         * @param chain
         *                the Type Chain<br>
         *                Usually call
         *                <code>chain.next().handle(cls, chain)</code> before do
         *                real handling.<br>
         *                If the <code>next()</code> handler has a result of
         *                <b>not null</b>, usually the handler simply return
         *                result.
         * @return retrieved instance
         * @throws AnnotationHandlingException
         */
        Object handle(Class<?> cls, TypeHandlerChain chain) throws AnnotationHandlingException;
}
