package cass.pure.ioc.initsupport;

import java.util.HashSet;
import java.util.Set;

import cass.pure.init.InitializeSupport;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.handlers.ConstructorFilter;

public class ConstructorInitializeSupport implements InitializeSupport {

	private Set<ConstructorFilter> set = new HashSet<ConstructorFilter>();

	@Override
	public void addClass(Class<?> cls) {
		if (ConstructorFilter.class.isAssignableFrom(cls)) {
			try {
				Object inst = cls.newInstance();
				set.add((ConstructorFilter) inst);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void doInit(String[] args) {
		for (ConstructorFilter filter : set) {
			IOCController.register(filter);
		}
	}

	@Override
	public double priority() {
		return 2.22;
	}

}
