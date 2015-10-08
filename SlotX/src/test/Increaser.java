package test;

import net.cassite.slot.anno.Input;
import net.cassite.slot.anno.Output;
import net.cassite.slot.anno.Pluggable;
import net.cassite.slot.anno.TwoWay;
import net.cassite.slot.exceptions.Result;

@Pluggable("a")
public class Increaser {

        @Input("numberInput")
        public void numberInput(int number) {
                numberOutput(number + 1);
        }

        @Output("numberOutput")
        public void numberOutput(int number) {
                throw new Result(number);
        }

        @TwoWay("doIncrease")
        public int doIncrease(int number, int res) {
                return number + 1;
        }
}
