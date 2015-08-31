package net.cassite.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.handlers.EmptyHandler;

public class ParamHandlerChain {

        private static final Logger LOGGER = Logger.getLogger(ParamHandlerChain.class);

        private final Iterator<ParamAnnotationHandler> it;

        public ParamHandlerChain(List<ParamAnnotationHandler> handlers, Annotation[] anns) {
                List<ParamAnnotationHandler> list = new ArrayList<ParamAnnotationHandler>();
                handlers.forEach(h -> {
                        if (h.canHandle(anns)) {
                                list.add(h);
                        }
                });
                list.add(EmptyHandler.getInstance());

                LOGGER.debug("Generate Param Chain with Handlers: " + list);

                it = list.iterator();
        }

        public ParamAnnotationHandler next() {
                return it.next();
        }
}
