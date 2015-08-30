package net.cassite.pure.ioc.handlers.setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.annotations.Use;
import net.cassite.pure.ioc.handlers.SetterAnnotationHandler;
import net.cassite.pure.ioc.handlers.SetterHandlerChain;
import net.cassite.style.reflect.MethodSupport;

/**
 * <br/>
 * Handler for Use annotation. <br/>
 * Use object of the type that the annotation designated.
 * 
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.Use
 *
 */
public class SetterUseHandler extends IOCController implements SetterAnnotationHandler {

        @Override
        public boolean canHandle(Set<Annotation> annotations) {
                for (Annotation ann : annotations) {
                        if (ann.annotationType() == Use.class) {
                                return true;
                        }
                }
                return false;
        }

        @Override
        public boolean handle(Object target, MethodSupport<Object, Object> setter, Set<Annotation> toHandle, SetterHandlerChain chain)
                        throws AnnotationHandlingException {
                if (chain.next().handle(target, setter, toHandle, chain)) {
                        return true;
                }

                Use use = null;
                for (Annotation ann : toHandle) {
                        if (ann.annotationType() == Use.class) {
                                use = (Use) ann;
                        }
                }

                Object obj = null;

                Class<?> clazz = use.clazz();
                if (clazz != Use.class)
                        obj = get(clazz);

                String constant = use.constant();
                if (!constant.equals(""))
                        obj = retrieveConstant(constant);

                String variable = use.variable();
                if (!variable.equals(""))
                        obj = retrieveVariable(variable);

                Object[] pv = new Object[] { obj };
                try {
                        setter.invoke(target, pv);
                } catch (Exception e) {
                        if (e instanceof InvocationTargetException) {
                                Throwable t = ((InvocationTargetException) e).getTargetException();
                                if (t instanceof RuntimeException)
                                        throw (RuntimeException) t;
                                if (t instanceof Error)
                                        throw (Error) t;
                        }
                        return false;
                }
                return true;
        }

}
