package net.cassite.slot.exceptions.parse;

import net.cassite.style.reflect.ClassSup;

public class MethodNotFoundParseException extends ParseException {

        /**
         * 
         */
        private static final long serialVersionUID = -3614976525797574886L;

        public MethodNotFoundParseException(ClassSup<Object> cls, String methodDef) {
                super("Cannot find method " + methodDef + " in class " + cls);
        }
}
