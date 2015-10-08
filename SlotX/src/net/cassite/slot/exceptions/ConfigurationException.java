package net.cassite.slot.exceptions;

public class ConfigurationException extends RuntimeException {

        /**
         * 
         */
        private static final long serialVersionUID = -7674513417736193897L;

        public ConfigurationException() {
                super();
        }

        public ConfigurationException(String msg) {
                super(msg);
        }

        public ConfigurationException(String msg, Throwable cause) {
                super(msg, cause);
        }

        public ConfigurationException(Throwable cause) {
                super(cause);
        }
}
