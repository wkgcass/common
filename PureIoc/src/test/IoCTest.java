package test;

import net.cassite.pure.ioc.AutoWire;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.annotations.Singleton;
import net.cassite.pure.ioc.annotations.Wire;

public class IoCTest {

        public static void main(String[] args) {
                IOCController.autoRegister();
                IOCController.closeRegistering();

                A a = new A();
                System.out.println(a);
                System.out.println(a.getB());
                System.out.println(a.getB().getC());
                System.out.println(a.getB().getC().getC());
        }

}

class A extends AutoWire {
        private B b;

        public B getB() {
                return b;
        }

        public void setB(B b) {
                this.b = b;
        }
}

@Wire
class B {
        private C c;

        public C getC() {
                return c;
        }

        public void setC(C c) {
                this.c = c;
        }
}

@Singleton
class C extends AutoWire {
        private C c;

        public C getC() {
                return c;
        }

        public void setC(C c) {
                this.c = c;
        }
}