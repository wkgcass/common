package net.cassite.slot.exceptions.parse.duplicated;

public class DuplicatedModuleNameParseException extends DuplicatedParseException {

        /**
         * 
         */
        private static final long serialVersionUID = -6807432585484491793L;

        public DuplicatedModuleNameParseException(String parsingModule, String duplicateName) {
                super("module", parsingModule, "module", duplicateName);
        }
}
