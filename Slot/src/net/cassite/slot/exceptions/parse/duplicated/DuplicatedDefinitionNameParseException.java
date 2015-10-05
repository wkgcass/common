package net.cassite.slot.exceptions.parse.duplicated;

public class DuplicatedDefinitionNameParseException extends DuplicatedParseException {

        /**
         * 
         */
        private static final long serialVersionUID = -3504506953910542623L;

        public DuplicatedDefinitionNameParseException(String parsingModule, String duplicateName) {
                super("module", parsingModule, "def", duplicateName);
        }
}
