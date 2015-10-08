package net.cassite.slot.anno;

import java.lang.reflect.Method;

public class SlotAnnoUtils {
        private SlotAnnoUtils() {
        }

        public static boolean isSlot(Method slot) {
                return isInput(slot) || isOutput(slot) || isTwoWay(slot);
        }

        public static boolean isInput(Method slot) {
                return slot.getParameterCount() == 1 && slot.getReturnType() == Void.TYPE && null != inputName(slot);
        }

        public static boolean isOutput(Method slot) {
                return slot.getParameterCount() == 1 && slot.getReturnType() == Void.TYPE && null != outputName(slot);
        }

        public static boolean isTwoWay(Method slot) {
                return slot.getParameterCount() == 2 && slot.getParameterTypes()[1] == slot.getReturnType() && null != twoWayName(slot);
        }

        public static String inputName(Method slot) {
                Input input = slot.getAnnotation(Input.class);
                return null == input ? null : input.value();
        }

        public static String outputName(Method slot) {
                Output output = slot.getAnnotation(Output.class);
                return null == output ? null : output.value();
        }

        public static String twoWayName(Method slot) {
                TwoWay twoWay = slot.getAnnotation(TwoWay.class);
                return null == twoWay ? null : twoWay.value();
        }

        public static String slotName(Method slot) {
                if (inputName(slot) != null)
                        return inputName(slot);
                if (outputName(slot) != null)
                        return outputName(slot);
                return twoWayName(slot);
        }

        public static boolean isPluggable(Class<?> cls) {
                return null != pluggableName(cls);
        }

        public static String pluggableName(Class<?> cls) {
                Pluggable p = cls.getAnnotation(Pluggable.class);
                return p == null ? null : p.value();
        }
}
