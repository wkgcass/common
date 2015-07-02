package cass.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import cass.pure.ioc.handlers.EmptyHandler;

public class SetterHandlerChain {
	private Set<SetterAnnotationHandler> set;
	private SetterAnnotationHandler current=null;
	public SetterHandlerChain(Set<SetterAnnotationHandler> handlers, Set<Annotation> anns){
		set=new HashSet<SetterAnnotationHandler>();
		for(SetterAnnotationHandler ah:handlers){
			if(ah.canHandle(anns)){
				set.add(ah);
			}
		}
	}
	public SetterAnnotationHandler next(){
		if(current==null){
			SetterAnnotationHandler ret=EmptyHandler.getInstance();
			for(SetterAnnotationHandler ah : set){
				if(ah.priority()<ret.priority()){
					ret=ah;
				}
			}
			current=ret;
			return ret;
		}
		SetterAnnotationHandler ret=EmptyHandler.getInstance();
		for(SetterAnnotationHandler ah : set){
			if(ah.priority()<ret.priority() && ah.priority()>current.priority()){
				ret=ah;
			}
		}
		current=ret;
		return ret;
	}
}
