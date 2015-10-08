package net.cassite.slot;

import java.util.HashMap;
import java.util.Map;

import net.cassite.slot.configuration.SlotInstance;
import net.cassite.slot.configuration.SlotType;

public class Module {
        String prototype;

        /**
         * (pluggable -> (instanceName, proxy))
         */
        Map<String, Map<String, Proxy>> proxyMap = new HashMap<>();
        Map<String, SlotInstance> exposure = new HashMap<>();

        protected Module() {
        }

        public String getPrototype() {
                return prototype;
        }

        public Input<Object> input(String name) {
                return input(name, Object.class);
        }

        public <T> Input<T> input(String name, Class<T> inputType) {
                SlotInstance slotInstance = exposure.get(name);
                if (slotInstance == null)
                        throw new RuntimeException("exposed slot not found");
                if (slotInstance.slot.type != SlotType.Input)
                        throw new RuntimeException("exposed slot " + name + " is not Input");

                return new Input<>(proxyMap.get(slotInstance.pluggable).get(slotInstance.instance).getProxy(), slotInstance.slot.method, inputType);
        }

        @SuppressWarnings("unchecked")
        public <T> TwoWay<Object, T> twoWay(String name) {
                return (TwoWay<Object, T>) twoWay(name, Object.class, Object.class);
        }

        public <T, R> TwoWay<T, R> twoWay(String name, Class<T> inputType, Class<R> resultType) {
                SlotInstance slotInstance = exposure.get(name);
                if (slotInstance == null)
                        throw new RuntimeException("exposed slot not found");
                if (slotInstance.slot.type != SlotType.TwoWay)
                        throw new RuntimeException("exposed slot " + name + " is not Input");

                return new TwoWay<>(proxyMap.get(slotInstance.pluggable).get(slotInstance.instance).getProxy(), slotInstance.slot.method, inputType,
                                resultType);
        }
}
