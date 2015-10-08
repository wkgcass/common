package test;

import net.cassite.slot.Module;
import net.cassite.slot.ModuleFactory;
import net.cassite.slot.exceptions.Result;
import net.cassite.slot.lang.Define;
import net.cassite.slot.lang.Input;
import net.cassite.slot.lang.LangConfiguration;
import net.cassite.slot.lang.Output;
import net.cassite.slot.lang.Pluggable;
import net.cassite.slot.lang.TwoWay;

public class TestAll {
        public static void test() {
                LangConfiguration configuration = new LangConfiguration();
                Define define = new Define(configuration);
                Pluggable a = define.$("a", Increaser.class);
                Input in = a.input("in", "numberInput");
                Output a_out = a.output("a_out", "numberOutput");
                TwoWay twoWay = a.twoWay("twoWay", "doIncrease");

                Pluggable b = define.$("b", Decreaser.class);
                Input b_in = b.input("b_in", "numberInput");
                Output out = b.output("out", "numberOutput");
                TwoWay numberProvider = b.twoWay("numberProvider", "doDecrease");

                a_out.sendTo(b_in, null, null).prototype("x");
                twoWay.sendTo(numberProvider, null, null).prototype("x");

                in.expose("in", "x", null);
                twoWay.expose("twoWayIn", "x", null);

                ModuleFactory factory = new ModuleFactory(configuration);
                Module module = factory.build("x");

                try {
                        module.input("in").send(5);
                } catch (Result e) {
                        System.out.println(e.res);
                }

                System.out.println(module.twoWay("twoWayIn").send(2));
        }
}
