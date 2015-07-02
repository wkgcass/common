package cass.pure.ioc.initsupport;

import java.util.HashSet;
import java.util.Set;

import cass.pure.init.InitializeSupport;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.handlers.TypeAnnotationHandler;

public class TypeInitializeSupport implements InitializeSupport {

	private Set<TypeAnnotationHandler> handlers = new HashSet<TypeAnnotationHandler>();

	@Override
	public void addClass(Class<?> cls) {
		if (TypeAnnotationHandler.class.isAssignableFrom(cls)) {
			try {
				Object o = cls.newInstance();
				handlers.add((TypeAnnotationHandler) o);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void doInit(String[] args) {
		for (TypeAnnotationHandler ah : handlers) {
			IOCController.register(ah);
		}
	}

	@Override
	public double priority() {
		return 2.28;
	}

}
