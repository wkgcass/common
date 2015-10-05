package net.cassite.slot.exceptions.parse.duplicated;

public class DuplicatedOutNameParseException extends DuplicatedParseException {

        /**
         * 
         */
        private static final long serialVersionUID = -7594222299008134239L;

        public DuplicatedOutNameParseException(String parsingDefinition, String duplicatedName) {
                super("definition", parsingDefinition, "out", duplicatedName);
        }
}
