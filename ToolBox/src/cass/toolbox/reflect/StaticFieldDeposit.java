package cass.toolbox.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Store <b>static</b> fields that are accessible through 'this' pointer.
 * @author wkgcass
 *
 */
public class StaticFieldDeposit {
	private Class<?> dep;
	private boolean privatE=true;
	private boolean pkgAccess=true;
	private boolean protecteD=true;
	protected Set<Field> fields=new HashSet<Field>();
	public StaticFieldDeposit(Class<?> dep){
		this(dep,true);
	}
	public StaticFieldDeposit(Class<?> dep, boolean addAll){
		if(null==dep) throw new NullPointerException();
		this.dep=dep;
		if(addAll){
			this.addAll();
		}
	}
	public void addAll(){
		Class<?> tmp=this.dep;
		while(tmp!=null){
			Field[] fs=tmp.getDeclaredFields();
			for(Field f:fs){
				try{
					this.add(f);
				}catch(IllegalArgumentException e){}
			}
			tmp=tmp.getSuperclass();
		}
	}
	public void add(String name){
		Class<?> tmp=this.dep;
		while(tmp!=null){
			Field[] fs=tmp.getDeclaredFields();
			for(Field f:fs){
				if(name.equals(f.getName())){
					try{
						this.add(f);
					}catch(IllegalArgumentException e){}
				}
			}
			tmp=tmp.getSuperclass();
		}
	}
	public void add(Field f){
		if(!Reflection.canAccessWithThisPointer(this.dep, f)){
			throw new IllegalArgumentException(f+" Not accessible by "+this.dep+" with this pointer.");
		}
		if(!Modifier.isStatic(f.getModifiers())) throw new IllegalArgumentException(f+" is not a static field.");
		this.fields.add(f);
	}
	public void removeAll(){
		this.fields.clear();
	}
	public void removeAll(String name){
		Iterator<Field> it=this.fields.iterator();
		while(it.hasNext()){
			Field f=it.next();
			if(f.getName().equals(name)){
				it.remove();
				return;
			}
		}
	}
	public void remove(Field f){
		Iterator<Field> it=this.fields.iterator();
		while(it.hasNext()){
			Field ff=it.next();
			if(ff==f){
				it.remove();
				return;
			}
		}
	}
	public Object get(String name) throws IllegalAccessException{
		for(Field f:this.fields){
			if(name.equals(f.getName())){
				if(!this.privatE && Modifier.isPrivate(f.getModifiers())) throw new IllegalAccessException(f+" is private");
				if(!this.pkgAccess && 
						(Modifier.isPrivate(f.getModifiers()) 
								&& Modifier.isProtected(f.getModifiers())
								&& Modifier.isPublic(f.getModifiers()))
						) throw new IllegalAccessException(f+" is package accessible.");
				if(!this.protecteD && Modifier.isProtected(f.getModifiers())) throw new IllegalAccessException(f+" is protected");
				f.setAccessible(true);
				try{
					return this.get(f);
				}finally{
					f.setAccessible(false);
				}
			}
		}
		throw new IllegalArgumentException("No matched field found.");
	}
	protected Object get(Field f) throws IllegalAccessException{
		return f.get(null);
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
