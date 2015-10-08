package net.cassite.slot.exceptions;

public class Result extends RuntimeException {

        /**
         * 
         */
        private static final long serialVersionUID = -1052458054761567133L;
        public final Object res;

        public Result(Object res) {
                this.res = res;
        }

        public String toString() {
                return this.getClass().getName() + " : " + res.toString();
        }
}
