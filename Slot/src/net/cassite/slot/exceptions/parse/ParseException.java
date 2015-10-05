package net.cassite.slot.exceptions.parse;

import net.cassite.slot.exceptions.SlotException;

public class ParseException extends SlotException {

        /**
         * 
         */
        private static final long serialVersionUID = -9116650983825664214L;

        public ParseException() {
                super();
        }

        public ParseException(String msg) {
                super(msg);
        }

        public ParseException(String msg, Throwable cause) {
                super(msg, cause);
        }

        public ParseException(Throwable cause) {
                super(cause);
        }

        public ParseException(String elementName, String attribute) {
                this("Syntax Error : Element " + elementName + " should contain an attribute named " + attribute);
        }
}
