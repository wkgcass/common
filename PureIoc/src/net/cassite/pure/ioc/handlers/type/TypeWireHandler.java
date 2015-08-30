package net.cassite.pure.ioc.handlers.type;

import java.lang.annotation.Annotation;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.AutoWire;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.annotations.Invoke;
import net.cassite.pure.ioc.annotations.Wire;
import net.cassite.pure.ioc.handlers.TypeAnnotationHandler;
import net.cassite.pure.ioc.handlers.TypeHandlerChain;

/**
 * Handler for Wire annotation. <br/>
 * if the class extends from AutoWire, this would simply return <br/>
 * else, all setters would be autowired.
 * 
 * @author wkgcass
 * 
 * @see cass.toolbox.ioc.annotations.Wire
 *
 */
public class TypeWireHandler extends IOCController implements TypeAnnotationHandler {

        private static final Logger logger = Logger.getLogger(TypeWireHandler.class);

        @Override
        public boolean canHandle(Annotation[] annotations) {
                for (Annotation ann : annotations) {
                        if (ann.annotationType() == Wire.class) {
                                return true;
                        }
                }
                return false;
        }

        @Override
        public Object handle(Class<?> cls, TypeHandlerChain chain) throws AnnotationHandlingException {
                logger.debug("Entered TypeWireHandler with args: \n\tcls:\t" + cls + "\n\tchain:\t" + chain);
                Object inst = null;
                try {
                        inst = chain.next().handle(cls, chain);
                } catch (AnnotationHandlingException e) {
                }

                logger.debug("start handling with TypeWireHandler");

                if (null == inst)
                        throw new AnnotationHandlingException("Instatiation failed");
                if (AutoWire.class.isAssignableFrom(cls))
                        return inst;
                /*******************************/
                logger.debug("--start wiring setters");
                // setters
                net.cassite.style.ptr<Object> $inst = ptr(inst);
                $(cls(inst.getClass()).setters()).forEach(m -> {
                        invokeSetter($($inst), m);
                });
                logger.debug("--finished wiring setters");
                /*******************************/
                logger.debug("--start invoking methods");
                // invoke
                $(cls(inst.getClass()).allMethods()).forEach(m -> {
                        if (m.isAnnotationPresent(Invoke.class) && !m.isStatic())
                                invokeMethod(m, $($inst));
                });
                logger.debug("--finished invoking methods");

                return inst;
        }

}
