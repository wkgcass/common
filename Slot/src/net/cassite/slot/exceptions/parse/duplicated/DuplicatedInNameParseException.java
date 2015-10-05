package net.cassite.slot.exceptions.parse.duplicated;

public class DuplicatedInNameParseException extends DuplicatedParseException {

        /**
         * 
         */
        private static final long serialVersionUID = -9160332536861113720L;

        public DuplicatedInNameParseException(String parsingDefinition, String duplicatedName) {
                super("definition", parsingDefinition, "in", duplicatedName);
        }
}
