package net.cassite.slot.lang;

import java.lang.reflect.Method;

import net.cassite.slot.configuration.SlotType;
import net.cassite.slot.exceptions.ConfigurationException;

public class Input {
        public final String name;
        public final Method method;
        public final Pluggable pluggable;

        Input(String name, Method method, Pluggable pluggable) {
                this.name = name;
                this.method = method;
                this.pluggable = pluggable;
                pluggable.configuration.mapSlot(pluggable.cls, name, SlotType.Input, method);
        }

        public ToMapModule receiveFrom(Output out, PluggableInstance from, PluggableInstance to) {
                return out.sendTo(this, from, to);
        }

        public void expose(String exposedName, String prototype, PluggableInstance instance) {
                if (instance != null && this.pluggable != instance.pluggable) {
                        throw new ConfigurationException("instance.pluggable != this.pluggable");
                }
                pluggable.configuration.expose(exposedName, prototype, pluggable.name, instance == null ? null : instance.name, name);
        }
}
