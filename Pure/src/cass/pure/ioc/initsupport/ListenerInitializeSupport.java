package cass.pure.ioc.initsupport;

import java.util.HashSet;
import java.util.Set;



import cass.pure.init.InitializeSupport;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.listener.IOCListener;

public class ListenerInitializeSupport implements InitializeSupport {

	private Set<IOCListener> handlers=new HashSet<IOCListener>();
	
	@Override
	public void addClass(Class<?> cls) {
		if(IOCListener.class.isAssignableFrom(cls)){
			try{
				Object o=cls.newInstance();
				handlers.add((IOCListener)o);
			}catch(Exception e){}
		}
	}

	@Override
	public void doInit(String[] args) {
		for(IOCListener listener:handlers){
			IOCController.register(listener);
		}
	}

	@Override
	public double priority() {
		return 2.42;
	}

}
