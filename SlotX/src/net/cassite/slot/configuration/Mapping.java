package net.cassite.slot.configuration;

public class Mapping {
        protected String inputPluggable;
        protected String inputInstanceName;
        protected String inputSlot;

        protected String outputPluggable;
        protected String outputInstanceName;
        protected String outputSlot;

        public void setInput(String inputPluggable, String inputInstanceName, String inputSlot) {
                this.inputPluggable = inputPluggable;
                this.inputSlot = inputSlot;
                this.inputInstanceName = inputInstanceName;
        }

        public void setOutput(String outputPluggable, String outputInstanceName, String outputSlot) {
                this.outputPluggable = outputPluggable;
                this.outputSlot = outputSlot;
                this.outputInstanceName = outputInstanceName;
        }

        public String getInputInstanceName() {
                return inputInstanceName;
        }

        public String getInputPluggable() {
                return inputPluggable;
        }

        public String getInputSlot() {
                return inputSlot;
        }

        public String getOutputInstanceName() {
                return outputInstanceName;
        }

        public String getOutputPluggable() {
                return outputPluggable;
        }

        public String getOutputSlot() {
                return outputSlot;
        }

        @Override
        public String toString() {
                return "(" + outputPluggable + "#" + outputInstanceName + "(" + outputSlot + ")=>(" + inputPluggable + "#" + inputInstanceName + "("
                                + inputSlot + "))";
        }
}
