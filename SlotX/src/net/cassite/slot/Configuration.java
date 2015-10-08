package net.cassite.slot;

import java.util.List;
import java.util.Map;

import net.cassite.slot.configuration.Mapping;
import net.cassite.slot.configuration.Slot;
import net.cassite.slot.configuration.SlotInstance;

public interface Configuration {
        Map<String, Class<?>> getPluggableMap();

        Map<Class<?>, Map<String, Slot>> getSlotMap();

        Map<String, List<Mapping>> getPluggingPrototypeConfiguration();

        Map<String, Map<String, SlotInstance>> getPrototypeExposure();
}
