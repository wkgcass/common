package cass.toolbox.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class ConstructorDeposit {
	private Set<Constructor<?>> cons=new HashSet<Constructor<?>>();
	private boolean privatE=true;
	private boolean pkgAccess=true;
	private boolean protecteD=true;
	public ConstructorDeposit(Class<?> cls){
		for(Constructor<?> con:cls.getDeclaredConstructors()){
			cons.add(con);
		}
		for(Constructor<?> con:cls.getConstructors()){
			cons.add(con);
		}
	}
	private void throW(Constructor<?> con) throws IllegalAccessException{
		if(!this.privatE && Modifier.isPrivate(con.getModifiers())) throw new IllegalAccessException(con+" is private");
		if(!this.pkgAccess && 
				(Modifier.isPrivate(con.getModifiers()) 
						&& Modifier.isProtected(con.getModifiers())
						&& Modifier.isPublic(con.getModifiers()))
				) throw new IllegalAccessException(con+" is package accessible.");
		if(!this.protecteD && Modifier.isProtected(con.getModifiers())) throw new IllegalAccessException(con+" is protected");
	}
	public Object newInstance() throws InstantiationException, IllegalAccessException, InvocationTargetException{
		return newInstance(new Object[0]);
	}
	public Object newInstance(Object...parameterValues) throws InstantiationException, IllegalAccessException, InvocationTargetException{
		if(parameterValues.length==0){
			for(Constructor<?> con:cons){
				if(con.getParameterTypes().length==0){
					throW(con);
					return this.newInstance(con, parameterValues);
				}
			}
		}
		for(Constructor<?> con:cons){
			boolean construct=true;
			for(int i=0;i<parameterValues.length;++i){
				try{
					if(null==parameterValues[i]) continue;
					if(Reflection.equalsIgnorePrimitive(parameterValues[i].getClass(), con.getParameterTypes()[i])){
						//same
						continue;
					}else{
						//not same
						if(Reflection.isLastParamAndIsArrayClass(con.getParameterTypes(), i)){
							//last param & class is array
							if((Reflection.allSubClassOf(con.getParameterTypes()[i].getComponentType(),parameterValues, i))){
								//last param & class is array & type matches
								continue;
							}else{
								//last param & class is array & type mismatch
								construct=false;
								break;
							}
						}else{
							//not last param or is not array
							construct=false;
							break;
						}
					}
				}catch(IndexOutOfBoundsException e){
					construct=false;
					break;
				}
			}
			if(construct){
				throW(con);
				return newInstance(con,parameterValues);
			}
		}
		throw new IllegalArgumentException("No matched constructor found.");
	}
	
	private Object newInstance(Constructor<?> con, Object[] parameterValues) throws InstantiationException, IllegalAccessException, InvocationTargetException{
		con.setAccessible(true);
		try{
			return con.newInstance(parameterValues);
		}finally{
			con.setAccessible(false);
		}
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
