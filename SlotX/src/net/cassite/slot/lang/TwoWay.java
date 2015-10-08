package net.cassite.slot.lang;

import java.lang.reflect.Method;

import net.cassite.slot.configuration.PluggableTwoWay;
import net.cassite.slot.configuration.SlotType;
import net.cassite.slot.configuration.TwoWayMapping;
import net.cassite.slot.exceptions.ConfigurationException;

public class TwoWay {
        public final String name;
        public final Method method;
        public final Pluggable pluggable;

        TwoWay(String name, Method method, Pluggable pluggable) {
                this.name = name;
                this.method = method;
                this.pluggable = pluggable;
                pluggable.configuration.mapSlot(pluggable.cls, name, SlotType.TwoWay, method);
        }

        public ToMapModule sendTo(TwoWay in, PluggableInstance from, PluggableInstance to) {
                TwoWayMapping mapping = new TwoWayMapping();
                if (from == null) {
                        // from new
                        // check pluggable
                        if (to == null) {
                                // to new
                                // do new.out->new.in
                                mapping.setOutput(new PluggableTwoWay(this.pluggable.name, null, this.name));
                                mapping.setInput(new PluggableTwoWay(in.pluggable.name, null, in.name));
                        } else {
                                // check pluggable
                                if (to.pluggable != in.pluggable) {
                                        throw new ConfigurationException("to.pluggable != in.pluggable");
                                } else {
                                        // do new.out->inst.in
                                        mapping.setOutput(new PluggableTwoWay(this.pluggable.name, null, this.name));
                                        mapping.setInput(new PluggableTwoWay(in.pluggable.name, to.name, in.name));
                                }
                        }
                } else {
                        // check pluggable
                        if (from.pluggable != this.pluggable) {
                                throw new ConfigurationException("from.pluggable != out.pluggable");
                        } else {
                                if (to == null) {
                                        // to new
                                        // do inst.out->new.in
                                        mapping.setOutput(new PluggableTwoWay(this.pluggable.name, from.name, this.name));
                                        mapping.setInput(new PluggableTwoWay(in.pluggable.name, null, in.name));
                                } else {
                                        // check pluggable
                                        if (to.pluggable != in.pluggable) {
                                                throw new ConfigurationException("to.pluggable != in.pluggable");
                                        } else {
                                                // do inst.out->inst.in
                                                mapping.setOutput(new PluggableTwoWay(this.pluggable.name, from.name, this.name));
                                                mapping.setInput(new PluggableTwoWay(in.pluggable.name, to.name, in.name));
                                        }
                                }
                        }
                }
                return new ToMapModule(mapping, this.pluggable.configuration);
        }

        public ToMapModule receiveFrom(TwoWay out, PluggableInstance from, PluggableInstance to) {
                return out.sendTo(this, from, to);
        }

        public void expose(String exposedName, String prototype, PluggableInstance instance) {
                if (instance != null && this.pluggable != instance.pluggable) {
                        throw new ConfigurationException("instance.pluggable != this.pluggable");
                }
                pluggable.configuration.expose(exposedName, prototype, pluggable.name, instance == null ? null : instance.name, name);
        }
}
