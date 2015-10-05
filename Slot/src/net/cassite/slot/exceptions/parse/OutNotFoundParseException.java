package net.cassite.slot.exceptions.parse;

public class OutNotFoundParseException extends ParseException {

        /**
         * 
         */
        private static final long serialVersionUID = 7314965829952467460L;

        public OutNotFoundParseException(String processing, String out) {
                super("Cannot find out " + out + " while processing " + processing);
        }
}
