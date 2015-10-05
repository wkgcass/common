package net.cassite.slot.exceptions.parse.duplicated;

import net.cassite.slot.exceptions.parse.ParseException;

public class DuplicatedParseException extends ParseException {

        /**
         * 
         */
        private static final long serialVersionUID = 6849652325223178284L;

        public DuplicatedParseException(String parsingElement, String parsingName, String duplicatedElement, String duplicatedName) {
                super("ccurred duplicated " + duplicatedElement + " name [" + duplicatedName + "] while parsing " + parsingElement + " ["
                                + parsingName + "]");
        }
}
