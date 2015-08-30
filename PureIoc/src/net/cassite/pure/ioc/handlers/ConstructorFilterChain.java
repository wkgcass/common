package net.cassite.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class ConstructorFilterChain {

        private static final Logger logger = Logger.getLogger(ConstructorFilterChain.class);

        private final Iterator<ConstructorFilter> it;

        public ConstructorFilterChain(List<ConstructorFilter> handlers, Set<Annotation> anns) {
                List<ConstructorFilter> list = new ArrayList<ConstructorFilter>();
                handlers.forEach(e -> {
                        if (e.canHandle(anns))
                                list.add(e);
                });
                list.add(EmptyHandler.getInstance());

                logger.debug("Generate Constructor Chain with Filters: " + list);

                it = list.iterator();
        }

        public ConstructorFilter next() {
                return it.next();
        }
}
