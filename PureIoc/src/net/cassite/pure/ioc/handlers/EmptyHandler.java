package net.cassite.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.style.reflect.ConstructorSup;
import net.cassite.style.reflect.MemberSup;
import net.cassite.style.reflect.MethodSupport;

/**
 * Handles every kind of annotations. <br/>
 * false/null/exception would be returned/thrown in order to end the
 * HandlingChain. <br/>
 * priority Double.MAX_VALUE
 * 
 * @author wkgcass
 *
 */
public class EmptyHandler implements SetterAnnotationHandler, ParamAnnotationHandler, TypeAnnotationHandler, ConstructorFilter {

        private static EmptyHandler inst = null;

        private EmptyHandler() {
        }

        public static EmptyHandler getInstance() {
                if (null == inst) {
                        synchronized (EmptyHandler.class) {
                                if (null == inst) {
                                        inst = new EmptyHandler();
                                }
                        }
                }
                return inst;
        }

        @Override
        public boolean canHandle(Set<Annotation> annotations) {
                return true;
        }

        @Override
        public boolean handle(Object target, MethodSupport<Object, Object> setter, Set<Annotation> toHandle, SetterHandlerChain chain)
                        throws AnnotationHandlingException {
                return false;
        }

        @Override
        public Object handle(MemberSup<?> caller, Class<?> cls, Annotation[] toHandle, ParamHandlerChain chain) throws AnnotationHandlingException {
                throw new IrrelevantAnnotationHandlingException();
        }

        @Override
        public boolean canHandle(Annotation[] annotations) {
                return true;
        }

        @Override
        public Object handle(Class<?> cls, TypeHandlerChain chain) throws AnnotationHandlingException {
                throw new IrrelevantAnnotationHandlingException();
        }

        @Override
        public ConstructorSup<Object> handle(List<ConstructorSup<Object>> cons, ConstructorFilterChain chain) throws AnnotationHandlingException {
                return null;
        }

}
