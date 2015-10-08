package test;

import net.cassite.slot.anno.AnnotationConfiguration;
import net.cassite.slot.configuration.PluggableInput;
import net.cassite.slot.configuration.PluggableOutput;
import net.cassite.slot.configuration.PluggableTwoWay;

public class TestAnno {
        public static void test() {
                AnnotationConfiguration configuration = new AnnotationConfiguration();
                configuration.register(Increaser.class);
                configuration.register(Decreaser.class);

                configuration.map2W("y", m -> {
                        m.setOutput(new PluggableTwoWay("b", null, "doDecrease"));
                        m.setInput(new PluggableTwoWay("a", "a1", "doIncrease"));
                });
                configuration.mapIO("x", m -> {
                        m.setOutput(new PluggableOutput("b", null, "numberOutput"));
                        m.setInput(new PluggableInput("a", "a1", "numberInput"));
                });
                configuration.mapIO("x", m -> {
                        m.setOutput(new PluggableOutput("a", "a1", "numberOutput"));
                        m.setInput(new PluggableInput("b", "b1", "numberInput"));
                });

                System.out.println(configuration.getPluggableMap());
                System.out.println(configuration.getSlotMap());
                System.out.println(configuration.getPluggingPrototypeConfiguration());
        }
}
