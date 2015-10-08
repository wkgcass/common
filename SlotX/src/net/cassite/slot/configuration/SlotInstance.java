package net.cassite.slot.configuration;

public class SlotInstance {
        public final String pluggable;
        public final String instance;
        public final String slotName;
        public final Slot slot;

        public SlotInstance(String pluggable, String instance, String slotName, Slot slot) {
                this.pluggable = pluggable;
                this.instance = instance;
                this.slotName = slotName;
                this.slot = slot;
        }
}
