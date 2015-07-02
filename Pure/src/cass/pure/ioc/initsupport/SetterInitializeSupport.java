package cass.pure.ioc.initsupport;

import java.util.HashSet;
import java.util.Set;



import cass.pure.init.InitializeSupport;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.handlers.SetterAnnotationHandler;

public class SetterInitializeSupport implements InitializeSupport {

	private Set<SetterAnnotationHandler> handlers=new HashSet<SetterAnnotationHandler>();
	
	@Override
	public void addClass(Class<?> cls) {
		if(SetterAnnotationHandler.class.isAssignableFrom(cls)){
			try{
				Object o=cls.newInstance();
				handlers.add((SetterAnnotationHandler)o);
			}catch(Exception e){}
		}
	}

	@Override
	public void doInit(String[] args) {
		for(SetterAnnotationHandler ah:handlers){
			IOCController.register(ah);
		}
	}

	@Override
	public double priority() {
		return 2.26;
	}

}
