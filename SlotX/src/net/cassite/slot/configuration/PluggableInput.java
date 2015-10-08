package net.cassite.slot.configuration;

public class PluggableInput {
        public final String pluggable;
        public final String input;
        public final String inputInstanceName;

        public PluggableInput(String pluggable, String inputInstanceName, String input) {
                this.pluggable = pluggable;
                this.input = input;
                this.inputInstanceName = inputInstanceName;
        }
}
