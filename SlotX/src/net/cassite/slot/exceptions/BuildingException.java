package net.cassite.slot.exceptions;

public class BuildingException extends RuntimeException {

        /**
         * 
         */
        private static final long serialVersionUID = 1980745706319809319L;

        public BuildingException() {
                super();
        }

        public BuildingException(String msg) {
                super(msg);
        }

        public BuildingException(String msg, Throwable cause) {
                super(msg, cause);
        }

        public BuildingException(Throwable cause) {
                super(cause);
        }
}
