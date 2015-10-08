package test;

import net.cassite.slot.lang.Define;
import net.cassite.slot.lang.Input;
import net.cassite.slot.lang.LangConfiguration;
import net.cassite.slot.lang.Output;
import net.cassite.slot.lang.Pluggable;
import net.cassite.slot.lang.PluggableInstance;
import net.cassite.slot.lang.TwoWay;

public class TestLang {
        public static void test() {
                LangConfiguration configLang = new LangConfiguration();
                Define define = new Define(configLang);
                Pluggable a = define.$("a", Increaser.class);
                Input a_numberInput = a.input("numberInput", "numberInput");
                Output a_numberOutput = a.output("numberOutput", "numberOutput");
                TwoWay a_doIncrease = a.twoWay("doIncrease", "doIncrease");

                PluggableInstance a1 = a.newInstance("a1");

                Pluggable b = define.$("b", Decreaser.class);
                Input b_numberInput = b.input("numberInput", "numberInput");
                Output b_numberOutput = b.output("numberOutput", "numberOutput");
                TwoWay b_doDecrease = b.twoWay("doDecrease", "doDecrease");

                PluggableInstance b1 = b.newInstance("b1");

                a_numberInput.receiveFrom(b_numberOutput, null, a1).prototype("x");
                b_numberInput.receiveFrom(a_numberOutput, a1, b1).prototype("x");
                a_doIncrease.receiveFrom(b_doDecrease, b1, null).prototype("y");

                System.out.println(configLang.getPluggableMap());
                System.out.println(configLang.getSlotMap());
                System.out.println(configLang.getPluggingPrototypeConfiguration());
        }
}
