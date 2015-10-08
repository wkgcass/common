package net.cassite.slot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.cassite.slot.Proxy.ObjectMethodPack;
import net.cassite.slot.configuration.Mapping;
import net.cassite.slot.configuration.Slot;
import net.cassite.slot.configuration.SlotInstance;
import net.cassite.slot.configuration.SlotType;
import net.cassite.slot.exceptions.BuildingException;

public class ModuleFactory {

        private final Logger LOGGER = Logger.getLogger(ModuleFactory.class);

        private final Map<String, Class<?>> pluggableMap;
        private final Map<Class<?>, Map<String, Slot>> slotMap;
        private final Map<String, List<Mapping>> pluggingPrototypeConfiguration;
        private final Map<String, Map<String, SlotInstance>> prototypeExposure;

        public ModuleFactory(Configuration configuration) {
                LOGGER.info("Creating ModuleFactory with " + configuration);
                // pluggable
                Map<String, Class<?>> clonedPluggableMap = new HashMap<>();
                configuration.getPluggableMap().forEach(clonedPluggableMap::put);
                this.pluggableMap = clonedPluggableMap;

                // slot
                Map<Class<?>, Map<String, Slot>> clonedSlotMap = new HashMap<>();
                configuration.getSlotMap().forEach((k, v) -> {
                        Map<String, Slot> subMap = new HashMap<>();
                        clonedSlotMap.put(k, subMap);
                        v.forEach(subMap::put);
                });
                this.slotMap = clonedSlotMap;

                // prototype
                Map<String, List<Mapping>> clonedMap = new HashMap<>();
                configuration.getPluggingPrototypeConfiguration().forEach((k, v) -> {
                        List<Mapping> list = new ArrayList<>();
                        clonedMap.put(k, list);
                        v.forEach(list::add);
                });
                this.pluggingPrototypeConfiguration = clonedMap;

                LOGGER.debug("configured prototypes are " + pluggingPrototypeConfiguration.keySet());

                // exposure
                Map<String, Map<String, SlotInstance>> clonedExposureMap = new HashMap<>();
                configuration.getPrototypeExposure().forEach((k, v) -> {
                        Map<String, SlotInstance> tmpMap = new HashMap<>();
                        v.forEach(tmpMap::put);
                        clonedExposureMap.put(k, tmpMap);
                });
                prototypeExposure = clonedExposureMap;

                LOGGER.info("Creation Complete");
        }

        private List<Mapping> retrieveMappings(String prototype) {
                List<Mapping> mapping = pluggingPrototypeConfiguration.get(prototype);
                if (mapping == null)
                        throw new BuildingException("Prototype " + prototype + " not found");
                return mapping;
        }

        private void fillModule(final Module module, final List<Mapping> mappings, final Map<String, SlotInstance> exposure) {
                mappings.forEach(mapping -> {

                        LOGGER.debug("building mapping " + mapping + " ......");

                        String inputPluggable = mapping.getInputPluggable();
                        String inputInstance = mapping.getInputInstanceName();
                        String inputSlot = mapping.getInputSlot();

                        String outputPluggable = mapping.getOutputPluggable();
                        String outputInstance = mapping.getOutputInstanceName();
                        String outputSlot = mapping.getOutputSlot();

                        Map<String, Proxy> outMap;
                        if (module.proxyMap.containsKey(outputPluggable)) {
                                outMap = module.proxyMap.get(outputPluggable);
                        } else {
                                outMap = new HashMap<>();
                                module.proxyMap.put(outputPluggable, outMap);
                        }
                        Map<String, Proxy> inMap;
                        if (module.proxyMap.containsKey(inputPluggable)) {
                                inMap = module.proxyMap.get(inputPluggable);
                        } else {
                                inMap = new HashMap<>();
                                module.proxyMap.put(inputPluggable, inMap);
                        }

                        Proxy outProxy;
                        Class<?> outputCls = pluggableMap.get(outputPluggable);
                        if (outMap.containsKey(outputInstance)) {
                                outProxy = outMap.get(outputInstance);
                        } else {
                                LOGGER.debug("proxy(" + outputPluggable + "," + outputInstance + ") not found, do creation");
                                outProxy = new Proxy(outputPluggable, outputInstance, outputCls);
                                outMap.put(outputInstance, outProxy);
                        }
                        LOGGER.debug("retrieved output proxy is " + outProxy);

                        Proxy inputProxy;
                        Class<?> inputCls = pluggableMap.get(inputPluggable);
                        if (inMap.containsKey(inputInstance)) {
                                inputProxy = inMap.get(inputInstance);
                        } else {
                                LOGGER.debug("proxy(" + inputPluggable + "," + inputInstance + ") not found, do creation");
                                inputProxy = new Proxy(inputPluggable, inputInstance, inputCls);
                                inMap.put(inputInstance, inputProxy);
                        }
                        LOGGER.debug("retrieved input proxy is " + inputProxy);

                        Slot outSlot = slotMap.get(outputCls).get(outputSlot);
                        Slot inSlot = slotMap.get(inputCls).get(inputSlot);
                        if (outSlot.type == SlotType.Output) {
                                LOGGER.debug("slot type is output");
                                // output
                                outProxy.out.put(outSlot.method, new ObjectMethodPack(module, inputPluggable, inputInstance, inSlot.method));
                        } else if (outSlot.type == SlotType.TwoWay) {
                                LOGGER.debug("slot type is twoWay");
                                // twoway
                                outProxy.twoWay.put(outSlot.method, new ObjectMethodPack(module, inputPluggable, inputInstance, inSlot.method));
                        }
                });

                LOGGER.debug("filling in exposure configurations");

                exposure.forEach(module.exposure::put);

                LOGGER.debug("module is filled with " + module.proxyMap.size() + (module.proxyMap.size() <= 1 ? " proxy handler" : " proxy handlers")
                                + ", and " + module.exposure.size()
                                + (module.exposure.size() <= 1 ? " exposure configuration" : " exposure configurations"));

        }

        public Module build(String prototype) {
                return build(prototype, Module.class);
        }

        @SuppressWarnings("unchecked")
        public <T extends Module> T build(String prototype, Class<T> moduleType) {

                LOGGER.info("Building module from prototype " + prototype + " with class " + moduleType.getName());

                List<Mapping> mapping = retrieveMappings(prototype);
                Module module;
                try {
                        module = moduleType.newInstance();
                        fillModule(module, mapping, this.prototypeExposure.get(prototype));

                        LOGGER.info("Building Complete");

                        return (T) module;
                } catch (InstantiationException | IllegalAccessException e) {
                        throw new BuildingException(e);
                }
        }
}
