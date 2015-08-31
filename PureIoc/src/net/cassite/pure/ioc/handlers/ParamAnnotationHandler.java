package net.cassite.pure.ioc.handlers;

import java.lang.annotation.Annotation;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.style.reflect.MemberSup;

/**
 * retrieve an instance of given class
 * 
 * @author wkgcass
 *
 */
public interface ParamAnnotationHandler {
        /**
         * one of given annotations can be handled
         * 
         * @param annotations
         *                a summary of annotations
         * @return true if one of the annos can be handled, false otherwise.
         */
        boolean canHandle(Annotation[] annotations);

        /**
         * retrieve an instance of given <code>cls</code>
         * 
         * @param caller
         *                the member calling for arguments
         * @param cls
         *                class of instance to retrieve
         * @param toHandle
         *                annotations
         * @param chain
         *                the Param Chain<br>
         *                Usually call
         *                <code>chain.next().handle(caller, cls, toHandle, chain)</code>
         *                before do real handling.<br>
         *                The <code>next()</code> handler may throw
         *                <code>IrrelevantAnnotationHandlingException</code>, it
         *                means the <code>next()</code> handler failed
         *                retrieving instance, and need current handler do
         *                handlings.
         * @return
         * @throws AnnotationHandlingException
         */
        Object handle(MemberSup<?> caller, Class<?> cls, Annotation[] toHandle, ParamHandlerChain chain) throws AnnotationHandlingException;
}
