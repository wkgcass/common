package test;

import net.cassite.pure.aop.AOP;
import net.cassite.pure.aop.AOPPoint;
import net.cassite.pure.aop.Weaver;
import net.cassite.pure.ioc.AutoWire;
import net.cassite.pure.ioc.IOCController;
import net.cassite.pure.ioc.annotations.Default;
import net.cassite.pure.ioc.annotations.Force;
import net.cassite.pure.ioc.annotations.Ignore;
import net.cassite.pure.ioc.annotations.Singleton;
import net.cassite.pure.ioc.annotations.Use;
import net.cassite.pure.ioc.annotations.Wire;

public class IOCTest {
        private IOCTest() {
        }

        public static void main(String[] args) {
                IOCController.autoRegister();
                IOCController.registerExternalClass(B.class);
                IOCController.closeRegistering();

                A a = new A();
                System.out.println(a);
                System.out.println(a.getB());
                System.out.println(a.getB().getC());
                System.out.println(a.getB().getC().getC());
                System.out.println(a.getB().getC().getD());
                ((TestInterface2) a.getB().getC().getD()).print();
                System.out.println(a.getE());
                System.out.println(a.getDbl());

                F f = AutoWire.get(F.class);
                System.out.println(f.getF());
                System.out.println(f.getD());
        }

}

class A extends AutoWire {
        private B b;
        @Ignore
        private TestInterface2 e;
        private double dbl;

        public B getB() {
                return b;
        }

        public void setB(B b) {
                this.b = b;
        }

        public TestInterface2 getE() {
                return e;
        }

        @Use(clazz = E.class)
        public void setE(TestInterface2 e) {
                this.e = e;
        }

        public double getDbl() {
                return dbl;
        }

        @Force("12.5")
        public void setDbl(double dbl) {
                this.dbl = dbl;
        }
}

//@Wire
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
        private TestInterface1 d;

        public C getC() {
                return c;
        }

        public void setC(C c) {
                this.c = c;
        }

        public TestInterface1 getD() {
                return d;
        }

        public void setD(TestInterface1 d) {
                this.d = d;
        }

}

@AOP(value = Interface1Weaver.class, useCglib = true)
class D implements TestInterface1 {
        private B b;

        public D() {
                AutoWire.wire(this);
        }

        public B getB() {
                return b;
        }

        public void setB(B b) {
                this.b = b;
        }

}

class Interface1Weaver extends Weaver implements TestInterface2 {
        @Override
        protected void before(AOPPoint point) {
                System.out.println("before");
        }

        @Override
        protected void after(AOPPoint point) {
                System.out.println("after");
        }

        @Override
        protected void exception(AOPPoint point) throws Throwable {
        }

        @Override
        public void print() {
                System.out.println("print from weaver");
        }
}

class E extends AutoWire implements TestInterface2 {
        @Override
        public void print() {
                System.out.println("print");
        }
}

@Wire
class F {
        private int f;
        private TestInterface1 d;

        public F() {
                this.f = 1;
        }

        @Default
        public F(@Force("1") int f) {
                this.f = f;
        }

        public int getF() {
                return f;
        }

        public TestInterface1 getD() {
                return d;
        }

        public void setD(@Use(clazz = D.class) TestInterface1 d) {
                this.d = d;
        }
}