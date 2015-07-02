package test;

import test.test1.T;
import cass.toolbox.advance.inh.Extends;
import cass.toolbox.advance.inh.MultipleInheritable;

public class Test extends MultipleInheritable {
	final static public Class<?> T_=T.class;
	private Extends t=new Extends(T_);
	public Test(){
		t.con("3");
	}
	
}
