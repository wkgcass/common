package net.cassite.slot.exceptions.parse.duplicated;

public class DuplicatedOuterInNameParseException extends DuplicatedParseException {

        /**
         * 
         */
        private static final long serialVersionUID = -434573349458131852L;

        public DuplicatedOuterInNameParseException(String moduleName, String inName) {
                super("module", moduleName, "outer.in", inName);
        }

}
