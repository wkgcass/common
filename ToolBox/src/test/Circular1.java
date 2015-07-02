package test;

import cass.toolbox.ioc.AutoWire;
import cass.toolbox.ioc.annotations.IsSingleton;
@IsSingleton
public class Circular1 extends AutoWire {
	private Circular2 c2;

	public void setC2(Circular2 c2) {
		this.c2 = c2;
	}

	public Circular2 getC2() {
		return c2;
	}
}
