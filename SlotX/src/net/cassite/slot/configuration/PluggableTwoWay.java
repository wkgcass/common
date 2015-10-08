package net.cassite.slot.configuration;

public class PluggableTwoWay {
        public final String pluggable;
        public final String twoWay;
        public final String twoWayInstanceName;

        public PluggableTwoWay(String pluggable, String twoWayInstanceName, String twoWay) {
                this.pluggable = pluggable;
                this.twoWay = twoWay;
                this.twoWayInstanceName = twoWayInstanceName;
        }
}
