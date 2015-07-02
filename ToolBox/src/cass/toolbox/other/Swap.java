package cass.toolbox.other;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
public class Swap {
	public static boolean swapObj(Object a, Object b){
		if(a.getClass()!=b.getClass()) return false;
		Class<?> cls=a.getClass();
		while(cls!=null){
			for(Field f : a.getClass().getDeclaredFields()){
					if(	!Modifier.isStatic(f.getModifiers())){
						f.setAccessible(true);
						try {
							Object objA=f.get(a);
							f.set(a, f.get(b));
							f.set(b, objA);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
							return false;
						} catch (IllegalAccessException e) {
							e.printStackTrace();
							return false;
						}
						f.setAccessible(false);
					}
				}
			cls=cls.getSuperclass();
		}
		return true;
	}
	public static boolean synchronizedSwapObj(Object a, Object b){
		synchronized(a){
			synchronized(b){
				if(a.getClass()!=b.getClass()) return false;
				Class<?> cls=a.getClass();
				while(cls!=null){
					for(Field f : a.getClass().getDeclaredFields()){
							if(	!Modifier.isStatic(f.getModifiers())){
								f.setAccessible(true);
								try {
									Object objA=f.get(a);
									f.set(a, f.get(b));
									f.set(b, objA);
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
									return false;
								} catch (IllegalAccessException e) {
									e.printStackTrace();
									return false;
								}
								f.setAccessible(false);
							}
						}
					cls=cls.getSuperclass();
				}
			}
		}
		return true;
	}
}
