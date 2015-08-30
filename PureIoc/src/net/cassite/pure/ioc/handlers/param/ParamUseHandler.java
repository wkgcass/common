package net.cassite.pure.ioc.handlers.param;

import java.lang.annotation.Annotation;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.annotations.Use;
import net.cassite.pure.ioc.handlers.IrrelevantAnnotationHandlingException;
import net.cassite.pure.ioc.handlers.ParamAnnotationHandler;
import net.cassite.pure.ioc.handlers.ParamHandlerChain;
import net.cassite.style.reflect.MemberSup;

/**
 * Handler for Use annotation. <br/>
 * returns the instance of the class "use" annotation represents.
 * 
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.Use
 *
 */
public class ParamUseHandler extends IOCController implements ParamAnnotationHandler {

        @Override
        public boolean canHandle(Annotation[] annotations) {
                for (Annotation ann : annotations) {
                        if (ann.annotationType() == Use.class) {
                                return true;
                        }
                }
                return false;
        }

        @Override
        public Object handle(MemberSup<?> caller, Class<?> cls, Annotation[] toHandle, ParamHandlerChain chain) throws AnnotationHandlingException {
                try {
                        return chain.next().handle(caller, cls, toHandle, chain);
                } catch (AnnotationHandlingException e) {
                }
                return If((Use) $(toHandle).findOne(a -> a.annotationType() == Use.class), use -> {
                        Class<?> clazz = use.clazz();
                        if (clazz != Use.class)
                                return get(clazz);
                        String constant = use.constant();
                        if (!constant.equals(""))
                                return retrieveConstant(constant);
                        String variable = use.variable();
                        if (!variable.equals(""))
                                return retrieveVariable(variable);
                        throw new AnnotationHandlingException("empty Use annotation");
                }).Else(() -> {
                        throw new IrrelevantAnnotationHandlingException();
                });
        }

}
