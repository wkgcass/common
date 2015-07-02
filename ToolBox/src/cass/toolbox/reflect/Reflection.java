package cass.toolbox.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class Reflection {
	public static void checkAccessible(Class<?> from, Member member, Class<? extends RuntimeException> classT){
		if(!isAccessible(from,member)){
			try {
				throw classT.getConstructor(String.class).newInstance("Cannot access "+member+" from "+from+".");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static boolean isAccessible(Class<?> from, Member member){
		//same class
		if(from==member.getDeclaringClass()) return true;
		//not same class & is private
		if(Modifier.isPrivate(member.getModifiers())) return false;
		//not same class & is public
		if(Modifier.isPublic(member.getModifiers())) return true;
		//not sub class & not private & not public
		if(!member.getDeclaringClass().isAssignableFrom(from)) return false;
		//is sub class & is protected
		if(Modifier.isProtected(member.getModifiers())) return true;
		//is sub class & is default(package access) & same package
		if(member.getDeclaringClass().getPackage()==from.getPackage()) return true;
		//is sub class & is default & not same package
		return false;
	}
	private static boolean canAccessFieldWithThisPointer(Class<?> from,Field field){
		while(from!=null){
			for(Field f:from.getDeclaredFields()){
				if(f.getName().equals(field.getName())){
					if(field.getDeclaringClass()==from){
						return true;
					}else{
						return false;
					}
				}
			}
			from=from.getSuperclass();
		}
		return false;
	}
	private static boolean canAccessMethodWithThisPointer(Class<?> from,Method method){
		while(from!=null){
			for(Method m:from.getDeclaredMethods()){
				if(m.getName().equals(method.getName())
						&&
					Arrays.equals(m.getParameterTypes(), method.getParameterTypes())
				){
					if(method.getDeclaringClass()==from){
						return true;
					}else{
						return false;
					}
				}
			}
			from=from.getSuperclass();
		}
		return false;
	}
	private static boolean canAccessConstructorWithThisPointer(Class<?> from,Constructor<?> con){
		while(from!=null){
			for(Constructor<?> c : from.getDeclaredConstructors()){
				if(Arrays.equals(c.getParameterTypes(), con.getParameterTypes())){
					if(con.getDeclaringClass()==from){
						return true;
					}else{
						return false;
					}
				}
			}
			from=from.getSuperclass();
		}
		return false;
	}
	public static boolean canAccessWithThisPointer(Class<?> from,Member member){
		//accessible
		if(!isAccessible(from,member)) return false;
		if(!member.getDeclaringClass().isAssignableFrom(from)) return false;
		if(member instanceof Field){
			return canAccessFieldWithThisPointer(from,(Field)member);
		}else if(member instanceof Method){
			return canAccessMethodWithThisPointer(from,(Method)member);
		}else if(member instanceof Constructor<?>){
			return canAccessConstructorWithThisPointer(from,(Constructor<?>)member);
		}else{
			return false;
		}
	}
	public static boolean isLastParam(Class<?>[] p, int paramIndex){
		if(paramIndex<0) return false;
		if(p.length==paramIndex+1) return true;
		return false;
	}
	public static boolean isLastParamAndIsArrayClass(Class<?>[] p, int paramIndex){
		if(paramIndex<0) return false;
		if(isLastParam(p,paramIndex)){
			return p[paramIndex].isArray();
		}else{
			return false;
		}
	}
	public static boolean allSubClassOf(Class<?> cls, Object[]arr){
		return allSubClassOf(cls,arr,0);
	}
	public static boolean allSubClassOf(Class<?> cls, Object[]arr, int index){
		for(int i=index;i<arr.length;++i){
			if(!cls.isAssignableFrom(arr[i].getClass())){
				return false;
			}
		}
		return true;
	}
	public static Class<?> allSameClass(Object[] arr){
		return allSameClass(arr,0);
	}
	public static Class<?> allSameClass(Object[] arr ,int index){
		Class<?> cls=arr[index].getClass();
		for(int i=index+1;i<arr.length;++i){
			if(arr[i].getClass()!=cls){
				return null;
			}
		}
		return cls;
	}
	
	public static boolean equalsIgnorePrimitive(Class<?> c, Class<?> c2){
		//equals
		if(c==c2) return true;
		//primitive
		if(c2.isPrimitive()){
			//let c1=primitive and c2=another
			Class<?> tmp=c;
			c=c2;
			c2=tmp;
		}
		if(c.isPrimitive()){
			if(c==int.class && c2==Integer.class){
				return true;
			}else if(c==char.class && c2==Character.class){
				return true;
			}else if(c==long.class && c2==Long.class){
				return true;
			}else if(c==short.class && c2==Short.class){
				return true;
			}else if(c==byte.class && c2==Byte.class){
				return true;
			}else if(c==boolean.class && c2==Boolean.class){
				return true;
			}else if(c==float.class && c2==Float.class){
				return true;
			}else if(c==double.class && c2==Double.class){
				return true;
			}else{
				return false;
			}
		}else{
			//not primitive & not equal
			return false;
		}
	}
	
	public static boolean isSetter(Method m){
		if(m.getReturnType()!=Void.TYPE) return false;
		if(m.getParameterTypes().length!=1) return false;
		if(!m.getName().startsWith("set")) return false;
		try{
			char c=m.getName().charAt(3);
			if(c<'A' || c>'Z') return false;
			return true;
		}catch(IndexOutOfBoundsException e){
			return false;
		}
		
	}
}
