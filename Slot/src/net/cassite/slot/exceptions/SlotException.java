package net.cassite.slot.exceptions;

public class SlotException extends RuntimeException {

        /**
         * 
         */
        private static final long serialVersionUID = -1881213072275326780L;

        public SlotException() {
                super();
        }

        public SlotException(String msg) {
                super(msg);
        }

        public SlotException(String msg, Throwable cause) {
                super(msg, cause);
        }

        public SlotException(Throwable cause) {
                super(cause);
        }
}
