package cass.pure.ioc.initsupport;

import java.lang.reflect.Field;
import java.util.Map;

import cass.pure.init.ApplicationEntrance;
import cass.pure.init.InitializeSupport;
import cass.pure.ioc.IOCController;

public class ConstantCloneSupport implements InitializeSupport {

	@Override
	public void addClass(Class<?> cls) {}

	@SuppressWarnings("unchecked")
	@Override
	public void doInit(String[] args) {
		try{
			Field f=ApplicationEntrance.class.getDeclaredField("constants");
			f.setAccessible(true);
			Map<String,Object> map=(Map<String, Object>) f.get(null);
			f.setAccessible(false);
			
			for(String s:map.keySet()){
				IOCController.registerConstant(s, map.get(s));
			}
		}catch(Exception e){e.printStackTrace();}
	}

	@Override
	public double priority() {
		return -20.2;
	}

}
