package net.cassite.slot.lang;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cassite.slot.Configuration;
import net.cassite.slot.configuration.Mapping;
import net.cassite.slot.configuration.Slot;
import net.cassite.slot.configuration.SlotInstance;
import net.cassite.slot.configuration.SlotType;
import net.cassite.slot.exceptions.ConfigurationException;

public class LangConfiguration implements Configuration {
        private final Map<String, Class<?>> pluggableMap = new HashMap<>();
        private final Map<Class<?>, Map<String, Slot>> slotMap = new HashMap<>();
        private final Map<String, List<Mapping>> pluggingPrototypeConfiguration = new HashMap<>();
        private final Map<String, Map<String, SlotInstance>> prototypeExposure = new HashMap<>();

        void mapPluggable(String name, Class<?> cls) {
                if (pluggableMap.containsKey(name)) {
                        throw new ConfigurationException("Duplicat name");
                }
                pluggableMap.put(name, cls);
        }

        void mapSlot(Class<?> cls, String name, SlotType type, Method method) {
                Map<String, Slot> map;
                if (slotMap.containsKey(cls)) {
                        map = slotMap.get(cls);
                } else {
                        map = new HashMap<>();
                        slotMap.put(cls, map);
                }
                map.put(name, new Slot(type, method));
        }

        void mapPrototype(String module, Mapping mapping) {
                List<Mapping> list;
                if (pluggingPrototypeConfiguration.containsKey(module)) {
                        list = pluggingPrototypeConfiguration.get(module);
                } else {
                        list = new ArrayList<Mapping>();
                        pluggingPrototypeConfiguration.put(module, list);
                }
                list.add(mapping);
        }

        void expose(String exposedName, String prototype, String pluggable, String instance, String slot) {
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
