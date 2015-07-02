package cass.toolbox.ioc.annotations.param;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import cass.toolbox.ioc.EmptyHandler;

public class ParamHandlerChain {
	private Set<ParamAnnotationHandler> set;
	private ParamAnnotationHandler current=null;
	public ParamHandlerChain(Set<ParamAnnotationHandler> handlers, Annotation[] anns){
		set=new HashSet<ParamAnnotationHandler>();
		for(ParamAnnotationHandler ah:handlers){
			if(ah.canHandle(anns)){
				set.add(ah);
			}
		}
	}
	public ParamAnnotationHandler next(){
		if(current==null){
			ParamAnnotationHandler ret=EmptyHandler.getInstance();
			for(ParamAnnotationHandler ah : set){
				if(ah.priority()<ret.priority()){
					ret=ah;
				}
			}
			current=ret;
			return ret;
		}
		ParamAnnotationHandler ret=EmptyHandler.getInstance();
		for(ParamAnnotationHandler ah : set){
			if(ah.priority()<ret.priority() && ah.priority()>current.priority()){
				ret=ah;
			}
		}
		current=ret;
		return ret;
	}
}
