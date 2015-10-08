package net.cassite.slot.configuration;

import java.lang.reflect.Method;

public class Slot {
        public final SlotType type;
        public final Method method;

        public Slot(SlotType type, Method method) {
                this.type = type;
                this.method = method;
        }

        @Override
        public String toString() {
                return "(" + type + " " + method + ")";
        }
}
