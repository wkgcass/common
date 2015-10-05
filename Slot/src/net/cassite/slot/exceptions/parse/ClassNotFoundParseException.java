package net.cassite.slot.exceptions.parse;

public class ClassNotFoundParseException extends ParseException {

        /**
         * 
         */
        private static final long serialVersionUID = 5659303852939217395L;

        public ClassNotFoundParseException(ClassNotFoundException ex) {
                super(ex);
        }
}
