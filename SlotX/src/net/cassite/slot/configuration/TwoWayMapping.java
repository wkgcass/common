package net.cassite.slot.configuration;

public class TwoWayMapping extends Mapping {

        public void setOutput(PluggableTwoWay output) {
                this.setOutput(output.pluggable, output.twoWayInstanceName, output.twoWay);
        }

        public void setInput(PluggableTwoWay input) {
                this.setInput(input.pluggable, input.twoWayInstanceName, input.twoWay);
        }

}
