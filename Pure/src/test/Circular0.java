package test;

import cass.pure.aop.AOP;
import cass.pure.ioc.AutoWire;

@AOP
public class Circular0 extends AutoWire {
	private Circular1 c1;

	public void setC1(Circular1 c1) {
		this.c1 = c1;
	}

	public Circular1 getC1() {
		return c1;
	}
}
