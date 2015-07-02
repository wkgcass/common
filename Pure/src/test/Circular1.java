package test;

import cass.pure.ioc.AutoWire;
import cass.pure.ioc.annotations.Singleton;
import cass.pure.ioc.annotations.Variable;

@Singleton
public class Circular1 extends AutoWire {
	private Circular2 c2;

	@Variable(name = "def")
	public int i = 5;

	public void setC2(Circular2 c2) {
		this.c2 = c2;
	}

	public Circular2 getC2() {
		return c2;
	}

	@Variable(name = "abc")
	public void print() {
		System.out.println("here");
	}
}
