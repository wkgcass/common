package net.cassite.pure.ioc;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.annotations.Invoke;
import net.cassite.style.Style;

/**
 * Base of pojo classes requiring auto wire.<br/>
 * Gives POJO (or a class with setters) the capability of autowiring. <br/>
 * Simply <b>new</b> a class, then all setters would be automatically called
 * <br/>
 * with corresponding parameters.
 * 
 * @author wkgcass
 *
 */
public abstract class AutoWire extends Style {

        private static final Logger logger = Logger.getLogger(AutoWire.class);

        protected AutoWire() {
                logger.debug("Constructing object " + this);
                
                wire(this);

                logger.debug("Finished Constructing " + this);
        }

        @SuppressWarnings("unchecked")
        public static <T> T get(Class<?> cls) {
                return (T) IOCController.get(cls);
        }

        public static void wire(Object o) {
                logger.debug("Start Wiring object " + o);
                IOCController.registerSingleton(o);
                $(cls(o).setters()).forEach(m -> {
                        IOCController.invokeSetter(o, m);
                });
                logger.debug("Finished Wiring " + o);

                logger.debug("Start Invoking methods of object " + o);
                $(cls(o).allMethods()).forEach(m -> {
                        if (m.annotation(Invoke.class) != null && !m.isStatic())
                                IOCController.invokeMethod(m, (Object) o);
                });
                logger.debug("Finished Invoking methods of object " + o);
        }
}
