package net.cassite.slot.configuration;

public class PluggableOutput {
        public final String pluggable;
        public final String output;
        public final String outputInstanceName;

        public PluggableOutput(String pluggable, String outputInstanceName, String output) {
                this.pluggable = pluggable;
                this.output = output;
                this.outputInstanceName = outputInstanceName;
        }
}
