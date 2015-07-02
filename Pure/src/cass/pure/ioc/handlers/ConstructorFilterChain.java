package cass.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import cass.pure.ioc.handlers.EmptyHandler;

public class ConstructorFilterChain {
	private Set<ConstructorFilter> set;
	private ConstructorFilter current=null;
	public ConstructorFilterChain(Set<ConstructorFilter>handlers, Set<Annotation> anns){
		set=new HashSet<ConstructorFilter>();
		for(ConstructorFilter ah:handlers){
			if(ah.canHandle(anns)){
				set.add(ah);
			}
		}
	}
	public ConstructorFilter next(){
		if(current==null){
			ConstructorFilter ret=EmptyHandler.getInstance();
			for(ConstructorFilter ah : set){
				if(ah.priority()<ret.priority()){
					ret=ah;
				}
			}
			current=ret;
			return ret;
		}
		ConstructorFilter ret=EmptyHandler.getInstance();
		for(ConstructorFilter ah : set){
			if(ah.priority()<ret.priority() && ah.priority()>current.priority()){
				ret=ah;
			}
		}
		current=ret;
		return ret;
	}
}
