package net.cassite.slot.lang;

public class PluggableInstance {
        public final String name;
        public final Pluggable pluggable;

        PluggableInstance(String name, Pluggable p) {
                this.name = name;
                this.pluggable = p;
        }
}
