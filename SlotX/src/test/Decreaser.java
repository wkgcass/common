package test;

import net.cassite.slot.anno.Input;
import net.cassite.slot.anno.Output;
import net.cassite.slot.anno.Pluggable;
import net.cassite.slot.anno.TwoWay;
import net.cassite.slot.exceptions.Result;

@Pluggable("b")
public class Decreaser {
        private int number;

        @Input("numberInput")
        public void numberInput(int number) {
                this.number = number;
                numberOutput(number - 1);
        }

        @Output("numberOutput")
        public void numberOutput(int number) {
                throw new Result(number);
        }

        @TwoWay("doDecrease")
        public int doDecrease(int number, int res) {
                return number - 1;
        }

        public int getNumber() {
                return number;
        }
}
