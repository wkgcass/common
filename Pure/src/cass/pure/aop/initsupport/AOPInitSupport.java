package cass.pure.aop.initsupport;

import java.util.HashSet;
import java.util.Set;

import cass.pure.aop.AOPController;
import cass.pure.aop.AOPHandler;
import cass.pure.init.InitializeSupport;

public class AOPInitSupport implements InitializeSupport {

	private Set<Class<?>> clsSet = new HashSet<Class<?>>();

	@Override
	public void doInit(String[] args) {
		for (Class<?> cls : clsSet) {
			try {
				AOPController.register((AOPHandler) cls.newInstance());
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void addClass(Class<?> cls) {
		if (AOPHandler.class.isAssignableFrom(cls)) {
			clsSet.add(cls);
		}
	}

	@Override
	public double priority() {
		return 2.32;
	}

}
