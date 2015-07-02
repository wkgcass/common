package cass.pure.aop;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class AOPBeforeChain {
	private Set<AOPHandler> set = new HashSet<AOPHandler>();
	private AOPHandler current = null;

	public AOPBeforeChain(Set<AOPHandler> handlerSet, Method method) {
		for (AOPHandler h : handlerSet) {
			if (h.canHandle(method)) {
				set.add(h);
			}
		}
		for (AOPHandler h : set) {
			if (current == null) {
				current = h;
				continue;
			}
			if (current.layer() > h.layer()
					&& !(h instanceof EmptyAOPHandlerLowLayer)) {
				current = h;
			}
		}
	}

	public AOPHandler next() {
		AOPHandler ret = current;
		AOPHandler tmp = null;
		for (AOPHandler h : set) {
			if (current.layer() < h.layer()) {
				if (null == tmp) {
					tmp = h;
				} else {
					if (h.layer() < tmp.layer()) {
						tmp = h;
					}
				}
			}
		}
		current = tmp;
		return ret;
	}
}
