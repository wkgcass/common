package net.cassite.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.cassite.pure.ioc.handlers.EmptyHandler;

public class ParamHandlerChain {

        private final Iterator<ParamAnnotationHandler> it;

        public ParamHandlerChain(List<ParamAnnotationHandler> handlers, Annotation[] anns) {
                List<ParamAnnotationHandler> list = new ArrayList<ParamAnnotationHandler>();
                handlers.forEach(h -> {
                        if (h.canHandle(anns))
                                list.add(h);
                });
                list.add(EmptyHandler.getInstance());
                it = list.iterator();
        }

        public ParamAnnotationHandler next() {
                return it.next();
        }
}
