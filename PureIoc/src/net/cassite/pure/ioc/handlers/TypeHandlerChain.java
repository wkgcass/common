package net.cassite.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.handlers.EmptyHandler;

public class TypeHandlerChain {

        private static final Logger logger = Logger.getLogger(TypeHandlerChain.class);

        private final Iterator<TypeAnnotationHandler> it;

        public TypeHandlerChain(List<TypeAnnotationHandler> handlers, Annotation[] anns) {

                List<TypeAnnotationHandler> list = new ArrayList<TypeAnnotationHandler>();
                handlers.forEach(h -> {
                        if (h.canHandle(anns))
                                list.add(h);
                });
                list.add(EmptyHandler.getInstance());

                logger.debug("Generate Type Chain with handlers " + list);

                it = list.iterator();
        }

        public TypeAnnotationHandler next() {
                return it.next();
        }
}
