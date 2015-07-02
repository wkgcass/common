package cass.toolbox.util;

public class Arrays_ {
	private Arrays_(){};
	public static Class<?>[] castToClass(Object[] arr){
		Class<?>[] arrret=new Class[arr.length];
		for(int i=0;i<arr.length;++i){
			arrret[i]=(Class<?>)arr[i];
		}
		return arrret;
	}
	public static boolean contains(Object[] arr, Object obj){
		for(Object o:arr){
			if(obj.equals(o)) return true;
		}
		return false;
	}
	public static boolean containsStrictly(Object[] arr, Object obj){
		for(Object o:arr){
			if(o==obj) return true;
		}
		return false;
	}
	public static int indexOf(Object[] arr , Object obj){
		for(int i=0;i<arr.length;++i){
			if(obj.equals(arr[i])){
				return i;
			}
		}
		return -1;
	}
}
