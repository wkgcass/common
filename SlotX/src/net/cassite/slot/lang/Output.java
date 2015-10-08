package net.cassite.slot.lang;

import java.lang.reflect.Method;

import net.cassite.slot.configuration.IOMapping;
import net.cassite.slot.configuration.PluggableInput;
import net.cassite.slot.configuration.PluggableOutput;
import net.cassite.slot.configuration.SlotType;
import net.cassite.slot.exceptions.ConfigurationException;

public class Output {
        public final String name;
        public final Method method;
        public final Pluggable pluggable;

        Output(String name, Method method, Pluggable pluggable) {
                this.name = name;
                this.method = method;
                this.pluggable = pluggable;
                pluggable.configuration.mapSlot(pluggable.cls, name, SlotType.Output, method);
        }

        public ToMapModule sendTo(Input in, PluggableInstance from, PluggableInstance to) {
                IOMapping mapping = new IOMapping();
                if (from == null) {
                        // from new
                        if (to == null) {
                                // to new
                                // new --> new
                                mapping.setOutput(new PluggableOutput(this.pluggable.name, null, this.name));
                                mapping.setInput(new PluggableInput(in.pluggable.name, null, in.name));
                        } else {
                                // do check
                                if (to.pluggable != in.pluggable) {
                                        throw new ConfigurationException("to.pluggable != in.pluggable");
                                }
                                // new --> inst
                                mapping.setOutput(new PluggableOutput(this.pluggable.name, null, this.name));
                                mapping.setInput(new PluggableInput(in.pluggable.name, to.name, in.name));
                        }
                } else {
                        // do check
                        if (from.pluggable != this.pluggable) {
                                throw new ConfigurationException("from.pluggable!=out.pluggable");
                        } else {
                                if (to == null) {
                                        // to new
                                        // inst --> new
                                        mapping.setOutput(new PluggableOutput(this.pluggable.name, from.name, this.name));
                                        mapping.setInput(new PluggableInput(in.pluggable.name, null, in.name));
                                } else {
                                        // do check
                                        if (to.pluggable != in.pluggable) {
                                                throw new ConfigurationException("to.pluggable != in.pluggable");
                                        }
                                        // inst --> inst
                                        mapping.setOutput(new PluggableOutput(this.pluggable.name, from.name, this.name));
                                        mapping.setInput(new PluggableInput(in.pluggable.name, to.name, in.name));
                                }
                        }
                }
                return new ToMapModule(mapping, this.pluggable.configuration);
        }
}
