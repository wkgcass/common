package net.cassite.slot.exceptions.parse.duplicated;

public class DuplicatedOuterOutNameParseException extends DuplicatedParseException {
        /**
         * 
         */
        private static final long serialVersionUID = 4536438445660561193L;

        public DuplicatedOuterOutNameParseException(String moduleName, String outName) {
                super("module", moduleName, "outer.out", outName);
        }
}
