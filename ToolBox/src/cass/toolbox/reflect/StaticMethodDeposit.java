package cass.toolbox.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author wkgcass
 *
 */
public class StaticMethodDeposit {
	protected Map<String,Set<Method>> methods = new HashMap<String,Set<Method>>();
	private Class<?> dep;
	private boolean privatE=true;
	private boolean pkgAccess=true;
	private boolean protecteD=true;
	public StaticMethodDeposit(Class<?> dep, boolean addAll){
		if(null==dep) throw new NullPointerException();
		this.dep=dep;
		if(addAll){
			this.addAll();
		}
	}
	public StaticMethodDeposit(Class<?> dep){
		this(dep,true);
	}
	public void addAll(){
		Class<?> tmp=this.dep;
		while(null!=tmp){
			Method[] ms=tmp.getDeclaredMethods();
			for(Method m:ms){
				try{
					this.add(m);
				}catch(IllegalArgumentException e){}
			}
			tmp=tmp.getSuperclass();
		}
	}
	public void addAll(String name){
		Class<?> tmp=this.dep;
		while(null!=tmp){
			Method[] ms=tmp.getDeclaredMethods();
			for(Method m:ms){
				if(name.equals(m.getName())){
					try{
						this.add(m);
					}catch(IllegalArgumentException e){}
				}
			}
			tmp=tmp.getSuperclass();
		}
	}
	public void add(Method m){
		if(!Reflection.canAccessWithThisPointer(this.dep, m)){
			throw new IllegalArgumentException("Not accessible with this pointer.");
		}
		if(!Modifier.isStatic(m.getModifiers())) throw new IllegalArgumentException(m+" is not a staic method.");
		Set<Method> set=methods.get(m.getName());
		if(null==set){
			set=new HashSet<Method>();
			methods.put(m.getName(), set);
		}
		set.add(m);
	}
	public void remove(Method m){
		Set<Method> set=this.methods.get(m);
		if(null==set) return;
		Iterator<Method> it=set.iterator();
		while(it.hasNext()){
			Method mm=it.next();
			if(mm==m){
				it.remove();
				return;
			}
		}
	}
	public void removeAll(String name){
		this.methods.remove(name);
	}
	public void removeAll(){
		this.methods.clear();
	}
	public Object invoke(String name) throws Throwable{
		return invoke(name,new Object[0]);
	}
	public Object invoke(String name, Object...parameterValues) throws Throwable{
		List<Method> list=new ArrayList<Method>();
		List<Method> list2=new ArrayList<Method>();
		if(this.methods.get(name)==null) throw new IllegalArgumentException("Method "+name+" do not exist.");
		for(Method m:this.methods.get(name)){
			if(m.getParameterTypes().length<=parameterValues.length){
				if(!this.privatE && Modifier.isPrivate(m.getModifiers())) continue;
				if(!this.pkgAccess && 
						(Modifier.isPrivate(m.getModifiers()) 
								&& Modifier.isProtected(m.getModifiers())
								&& Modifier.isPublic(m.getModifiers()))
						) continue;
				if(!this.protecteD && Modifier.isProtected(m.getModifiers())) continue;
				list.add(m);
			}
		}
		for(int i=0;i<parameterValues.length;++i){
			Iterator<Method> it=list.iterator();
			while(it.hasNext()){
				if(null==parameterValues[i]) continue;
				Method m=it.next();
				try{
					if(Reflection.equalsIgnorePrimitive(parameterValues[i].getClass(), m.getParameterTypes()[i])){
						//same
						continue;
					}else{
						//not same
						if(Reflection.isLastParamAndIsArrayClass(m.getParameterTypes(), i)){
							//last param & class is array
							if((Reflection.allSubClassOf(m.getParameterTypes()[i].getComponentType(),parameterValues, i))){
								//last param & class is array & type matches
								list2.add(m);
								it.remove();
							}else{
								//last param & class is array & type mismatch
								it.remove();
							}
						}else{
							//not last param or is not array
							it.remove();
						}
					}
				}catch(IndexOutOfBoundsException e){
					it.remove();
				}
			}
		}
		list.addAll(list2);
		if(list.size()==0 || list.size()>1) throw new IllegalArgumentException("can not find matched method");
		
		Class<?>[] pt=list.get(0).getParameterTypes();
		Object[] pv=new Object[pt.length];
		if(pt.length==0 && parameterValues.length==0) return this.invoke(list.get(0), pv);
		if(pt.length==parameterValues.length){
			if(Reflection.equalsIgnorePrimitive(pt[pt.length-1] , parameterValues[pt.length-1].getClass()))return this.invoke(list.get(0),parameterValues);
			else{
				for(int i=0;i<parameterValues.length-1;++i){
					pv[i]=parameterValues[i];
				}
				pv[pv.length-1]=new Object[]{parameterValues[parameterValues.length-1]};
				return this.invoke(list.get(0), pv);
			}
		}else{
			for(int i=0;i<parameterValues.length-1;++i){
				pv[i]=parameterValues[i];
			}
			Object[] arr=new Object[parameterValues.length-pv.length+1];
			for(int i=0;i<arr.length;++i){
				arr[i]=parameterValues[i+pv.length-1];
			}
			pv[pv.length-1]=arr;
			list.get(0).setAccessible(true);
			try{
				return this.invoke(list.get(0), pv);
			}catch(InvocationTargetException e){
				throw e.getCause();
			}finally{
				list.get(0).setAccessible(false);
			}
		}
		
	}
	protected Object invoke(Method method,Object...pv) throws IllegalAccessException, InvocationTargetException{
			return method.invoke(null, pv);
	}
	
	public void accessPrivate(boolean b){
		if(b){
			this.privatE=true;
			this.pkgAccess=true;
			this.protecteD=true;
		}else{
			this.privatE=false;
		}
	}
	public void accessPkgAccess(boolean b){
		if(b){
			this.pkgAccess=true;
			this.protecteD=true;
		}else{
			this.privatE=false;
			this.pkgAccess=false;
		}
	}
	public void accessProtected(boolean b){
		if(b){
			this.protecteD=true;
		}else{
			this.privatE=false;
			this.pkgAccess=false;
			this.protecteD=false;
		}
	}
}
