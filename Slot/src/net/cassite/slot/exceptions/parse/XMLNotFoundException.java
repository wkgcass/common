package net.cassite.slot.exceptions.parse;

public class XMLNotFoundException extends ParseException {

        /**
         * 
         */
        private static final long serialVersionUID = 6529887907128177201L;

        public XMLNotFoundException(String path) {
                super("Cannot find xml file at " + path);
        }
}
