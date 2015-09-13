package net.cassite.pure.ioc.handlers.type;

import java.lang.annotation.Annotation;

import org.apache.log4j.Logger;

import net.cassite.pure.aop.AOP;
import net.cassite.pure.aop.AOPController;
import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.pure.ioc.Utils;
import net.cassite.pure.ioc.handlers.TypeAnnotationHandler;
import net.cassite.pure.ioc.handlers.TypeHandlerChain;

public class TypeAOPHandler implements TypeAnnotationHandler {

        private static final Logger LOGGER = Logger.getLogger(TypeAOPHandler.class);

        @Override
        public boolean canHandle(Annotation[] annotations) {
                return Utils.getAnno(AOP.class, annotations) != null;
        }

        @Override
        public Object handle(Class<?> cls, TypeHandlerChain chain) throws AnnotationHandlingException {
                LOGGER.debug("Entered TypeAOPHandler with args: \n\tcls:\t" + cls + "\n\tchain:\t" + chain);
                Object o = chain.next().handle(cls, chain);
                LOGGER.debug("start handling with TypeAOPHandler");
                LOGGER.debug("retrieved instance is " + o);
                if (o == null)
                        throw new AnnotationHandlingException("cannot weave to null");
                Object r = AOPController.weave(o);
                LOGGER.debug("generated proxy object is " + r);
                return r;
        }

}
