package net.cassite.pure.ioc;

import org.apache.log4j.Logger;

import net.cassite.pure.ioc.annotations.Invoke;
import static net.cassite.style.Style.*;

/**
 * Base of pojo classes requiring auto wire.<br>
 * Gives POJO (or a class with setters) the capability of autowiring. <br>
 * Simply <b>new</b> a class, then all setters would be automatically called
 * <br>
 * with corresponding parameters.
 * 
 * @author wkgcass
 *
 */
public abstract class AutoWire {

        private static final Logger LOGGER = Logger.getLogger(AutoWire.class);

        protected AutoWire() {
                LOGGER.debug("Constructing object " + this);

                wire(this);

                LOGGER.debug("Finished Constructing " + this);
        }

        /**
         * Retrieve an instance of given class
         * 
         * @param cls
         *                instance of which to retrieve
         * @return retreived class
         */
        @SuppressWarnings("unchecked")
        public static <T> T get(Class<T> cls) {
                return (T) IOCController.get(cls);
        }

        /**
         * Invoke setters and methods with Invoke annotation on given object
         * 
         * @param o
         *                the object to wire
         */
        public static void wire(Object o) {
                if (!o.getClass().getName().contains("$$EnhancerByCGLIB$$")) { // prevent
                                                                               // wiring
                                                                               // cglib
                                                                               // generated
                                                                               // objects

                        LOGGER.debug("Start Wiring object " + o);
                        IOCController.registerSingleton(o);
                        $(cls(o).setters()).forEach(m -> IOCController.invokeSetter(o, m));
                        LOGGER.debug("Finished Wiring " + o);

                        LOGGER.debug("Start Invoking methods of object " + o);
                        $(cls(o).allMethods()).forEach(m -> {
                                if (m.annotation(Invoke.class) != null && !m.isStatic()) {
                                        IOCController.invokeMethod(m, (Object) o);
                                }
                        });
                        LOGGER.debug("Finished Invoking methods of object " + o);
                }
        }
}
