package net.cassite.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.handlers.EmptyHandler;

public class SetterHandlerChain {

        private static final Logger LOGGER = Logger.getLogger(SetterHandlerChain.class);

        private final Iterator<SetterAnnotationHandler> it;

        public SetterHandlerChain(List<SetterAnnotationHandler> handlers, Set<Annotation> anns) {
                List<SetterAnnotationHandler> list = new ArrayList<SetterAnnotationHandler>();
                handlers.forEach(h -> {
                        if (h.canHandle(anns)) {
                                list.add(h);
                        }
                });
                list.add(EmptyHandler.getInstance());

                LOGGER.debug("Generate Setter Chain With Handlers: " + list);

                it = list.iterator();
        }

        public SetterAnnotationHandler next() {
                return it.next();
        }
}
