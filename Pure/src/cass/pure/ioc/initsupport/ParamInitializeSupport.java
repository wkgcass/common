package cass.pure.ioc.initsupport;

import java.util.HashSet;
import java.util.Set;



import cass.pure.init.InitializeSupport;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.handlers.ParamAnnotationHandler;

public class ParamInitializeSupport implements InitializeSupport {

	private Set<ParamAnnotationHandler> handlers=new HashSet<ParamAnnotationHandler>();
	
	@Override
	public void addClass(Class<?> cls) {
		if(ParamAnnotationHandler.class.isAssignableFrom(cls)){
			try{
				Object o=cls.newInstance();
				handlers.add((ParamAnnotationHandler)o);
			}catch(Exception e){}
		}
	}

	@Override
	public void doInit(String[] args) {
		for(ParamAnnotationHandler ah:handlers){
			IOCController.register(ah);
		}
	}

	@Override
	public double priority() {
		return 2.24;
	}

}
