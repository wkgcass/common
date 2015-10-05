package net.cassite.pure.aop;

import net.cassite.pure.ioc.AutoWire;

import static net.cassite.style.aggregation.Aggregation.*;

import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * Controls AOP processes
 * 
 * @author wkgcass
 * @since 0.1.1
 *
 */
public abstract class AOPController {

        private static final Logger LOGGER = Logger.getLogger(AOPController.class);

        /**
         * retrieve proxy object
         * 
         * @param obj
         *                the object to enable aop on
         * @return proxy object
         */
        @SuppressWarnings("unchecked")
        public static <T> T weave(T obj) {
                LOGGER.debug("Weaving object " + obj);
                AOP aop = obj.getClass().getAnnotation(AOP.class);
                Weaver[] weavers;
                boolean useCglib = false;
                if (aop == null) {
                        return obj;
                } else {
                        weavers = new Weaver[aop.value().length];
                        useCglib = aop.useCglib();
                        $(aop.value()).forEach((e, i) -> {
                                weavers[$(i)] = (Weaver) AutoWire.get(e);
                        });
                }
                LOGGER.debug("retrieved weavers are " + Arrays.toString(weavers));
                if (useCglib) {
                        CglibHandler h = new CglibHandler(weavers, obj);
                        return (T) h.proxy();
                } else {
                        Handler h = new Handler(weavers, obj);
                        return (T) h.proxy();
                }
        }
}
