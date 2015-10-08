package net.cassite.slot.anno;

import static net.cassite.slot.anno.SlotAnnoUtils.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.cassite.slot.Configuration;
import net.cassite.slot.configuration.IOMapping;
import net.cassite.slot.configuration.Mapping;
import net.cassite.slot.configuration.Slot;
import net.cassite.slot.configuration.SlotInstance;
import net.cassite.slot.configuration.SlotType;
import net.cassite.slot.configuration.TwoWayMapping;
import net.cassite.slot.exceptions.ConfigurationException;

public class AnnotationConfiguration implements Configuration {
        private final Map<String, Class<?>> pluggableMap = new HashMap<>();
        private final Map<Class<?>, Map<String, Slot>> slotMap = new HashMap<>();
        private final Map<String, List<Mapping>> pluggingPrototypeConfiguration = new HashMap<>();
        private final Map<String, Map<String, SlotInstance>> prototypeExposure = new HashMap<>();

        public void register(Class<?> cls) {
                String name = pluggableName(cls);
                if (null == name) {
                        throw new ConfigurationException("The class is not Pluggable, present @Pluggable on the class");
                }
                if (pluggableMap.containsKey(name)) {
                        throw new ConfigurationException("Duplicate pluggable name");
                }
                pluggableMap.put(name, cls);

                HashMap<String, Slot> map = new HashMap<>();
                slotMap.put(cls, map);
                Method[] methods = cls.getMethods();
                for (Method m : methods) {
                        if (isInput(m)) {
                                map.put(inputName(m), new Slot(SlotType.Input, m));
                        } else if (isOutput(m)) {
                                map.put(outputName(m), new Slot(SlotType.Output, m));
                        } else if (isTwoWay(m)) {
                                map.put(twoWayName(m), new Slot(SlotType.TwoWay, m));
                        }
                }
        }

        public void expose(String exposedName, String prototype, String pluggable, String instance, String slot) {
                if (pluggableMap.containsKey(pluggable)) {
                        Class<?> cls = pluggableMap.get(pluggable);
                        Map<String, Slot> map = slotMap.get(cls);
                        if (map == null || !map.containsKey(slot)) {
                                throw new ConfigurationException("Slot " + slot + " not found");
                        } else {
                                Map<String, SlotInstance> instances;
                                if (prototypeExposure.containsKey(prototype)) {
                                        instances = prototypeExposure.get(prototype);
                                } else {
                                        instances = new HashMap<>();
                                        prototypeExposure.put(prototype, instances);
                                }
                                instances.put(exposedName, new SlotInstance(pluggable, instance, slot, map.get(slot)));
                        }
                } else {
                        throw new ConfigurationException("Pluggable " + pluggable + " not found");
                }
        }

        private void doMap(String moduleName, Mapping mapping) {
                List<Mapping> list;
                if (pluggingPrototypeConfiguration.containsKey(moduleName)) {
                        list = pluggingPrototypeConfiguration.get(moduleName);
                } else {
                        list = new ArrayList<>();
                        pluggingPrototypeConfiguration.put(moduleName, list);
                }
                list.add(mapping);
        }

        private void validatePluggable(Mapping m) {
                Class<?> outPluggable = pluggableMap.get(m.getOutputPluggable());
                if (null == outPluggable) {
                        throw new ConfigurationException("Pluggable name " + m.getOutputPluggable() + " not found");
                }
                Class<?> inPluggable = pluggableMap.get(m.getInputPluggable());
                if (null == inPluggable) {
                        throw new ConfigurationException("Pluggable name " + m.getInputPluggable() + " not found");
                }
        }

        private Method retrieveMethod(Mapping m, boolean isIn) {
                if (isIn) {
                        try {
                                Method in = slotMap.get(pluggableMap.get(m.getInputPluggable())).get(m.getInputSlot()).method;
                                if (null == in) {
                                        throw new ConfigurationException("Input Slot " + m.getInputSlot() + " not found");
                                }
                                return in;
                        } catch (NullPointerException e) {
                                throw new ConfigurationException("Input Slot " + m.getInputSlot() + " not found");
                        }
                } else {
                        try {
                                Method out = slotMap.get(pluggableMap.get(m.getOutputPluggable())).get(m.getOutputSlot()).method;
                                if (null == out) {
                                        throw new ConfigurationException("Output Slot " + m.getOutputSlot() + " not found");
                                }
                                return out;
                        } catch (NullPointerException e) {
                                throw new ConfigurationException("Output Slot " + m.getOutputSlot() + " not found");
                        }
                }
        }

        public void map(String moduleName, Consumer<Mapping> mapping) {
                Mapping m = new Mapping();
                mapping.accept(m);
                validatePluggable(m);

                Method in = retrieveMethod(m, true);
                Method out = retrieveMethod(m, false);

                if ((isOutput(out) && !isInput(in)) || (isTwoWay(out) && !isTwoWay(in)) || isInput(out)) {
                        throw new ConfigurationException("Cannot link slot " + m.getOutputSlot() + " to " + m.getInputSlot()
                                        + ", expecting Output-->Input or TwoWay-->TwoWay");
                }

                doMap(moduleName, m);
        }

        public void map2W(String moduleName, Consumer<TwoWayMapping> mapping) {
                TwoWayMapping m = new TwoWayMapping();
                mapping.accept(m);
                validatePluggable(m);

                Method in = retrieveMethod(m, true);
                Method out = retrieveMethod(m, false);

                if (!isTwoWay(out) || !isTwoWay(in)) {
                        throw new ConfigurationException("The slots are not both TwoWay, expecting TwoWay-->TwoWay");
                }

                doMap(moduleName, m);
        }

        public void mapIO(String moduleName, Consumer<IOMapping> mapping) {
                IOMapping m = new IOMapping();
                mapping.accept(m);
                validatePluggable(m);

                Method in = retrieveMethod(m, true);
                Method out = retrieveMethod(m, false);

                if (!isOutput(out)) {
                        throw new ConfigurationException(m.getOutputSlot() + " is not an output slot");
                }
                if (!isInput(in)) {
                        throw new ConfigurationException(m.getInputSlot() + " is not an input slot");
                }

                doMap(moduleName, m);
        }

        @Override
        public Map<String, Class<?>> getPluggableMap() {
                return pluggableMap;
        }

        @Override
        public Map<Class<?>, Map<String, Slot>> getSlotMap() {
                return slotMap;
        }

        @Override
        public Map<String, List<Mapping>> getPluggingPrototypeConfiguration() {
                return pluggingPrototypeConfiguration;
        }

        @Override
        public Map<String, Map<String, SlotInstance>> getPrototypeExposure() {
                return prototypeExposure;
        }

        public void doConfig(Config config) {
                config.doConfig(this);
        }
}
