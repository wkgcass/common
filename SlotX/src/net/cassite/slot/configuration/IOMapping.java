package net.cassite.slot.configuration;

public class IOMapping extends Mapping {

        public void setOutput(PluggableOutput output) {
                this.setOutput(output.pluggable, output.outputInstanceName, output.output);
        }

        public void setInput(PluggableInput input) {
                this.setInput(input.pluggable, input.inputInstanceName, input.input);
        }

}
