package cass.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import cass.pure.ioc.handlers.EmptyHandler;

public class TypeHandlerChain {
	private Set<TypeAnnotationHandler> set;
	private TypeAnnotationHandler current = null;

	public TypeHandlerChain(Set<TypeAnnotationHandler> handlers,
			Annotation[] anns) {
		set = new HashSet<TypeAnnotationHandler>();
		for (TypeAnnotationHandler ah : handlers) {
			if (ah.canHandle(anns)) {
				set.add(ah);
			}
		}
	}

	public TypeAnnotationHandler next() {
		if (current == null) {
			TypeAnnotationHandler ret = EmptyHandler.getInstance();
			for (TypeAnnotationHandler ah : set) {
				if (ah.priority() < ret.priority()) {
					ret = ah;
				}
			}
			current = ret;
			return ret;
		}
		TypeAnnotationHandler ret = EmptyHandler.getInstance();
		for (TypeAnnotationHandler ah : set) {
			if (ah.priority() < ret.priority()
					&& ah.priority() > current.priority()) {
				ret = ah;
			}
		}
		current = ret;
		return ret;
	}
}
