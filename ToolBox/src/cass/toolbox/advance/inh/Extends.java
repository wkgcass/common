package cass.toolbox.advance.inh;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import cass.toolbox.reflect.ConstructorDeposit;
import cass.toolbox.reflect.FieldDeposit;
import cass.toolbox.reflect.MethodDeposit;
/**
 * 
 * @author wkgcass
 *
 */
public class Extends {
	private Class<?> c;
	private ConstructorDeposit cd;
	private MethodDeposit md=null;
	private FieldDeposit fd=null;
	private Set<Extends> sups=new HashSet<Extends>();
	public Extends(Class<?> c){
		if(c.isAnnotation() 
				|| c.isAnonymousClass() 
				|| c.isArray() 
				|| c.isEnum() 
				|| c.isInterface() 
				|| c.isLocalClass() 
				|| c.isPrimitive() 
				|| c.isSynthetic() 
				|| c.isMemberClass()){
			throw new InheritanceRuntimeException("Unsupported Class Type "+c);
		}
		this.c=c;
		this.cd=new ConstructorDeposit(c);
		this.cd.accessPrivate(false);
		if(MultipleInheritable.class.isAssignableFrom(c)){
			//multiple inheritable
			Field[] fields=c.getDeclaredFields();
			for(Field f : fields){
				if(Modifier.isStatic(f.getModifiers())
						&&
						Modifier.isFinal(f.getModifiers())
						&&
						Class.class==f.getType()
						){
					try{
						this.sups.add(new Extends((Class<?>)f.get(null)));
					}catch(Exception e){
						throw new InheritanceRuntimeException(e);
					}
				}
			}
		}
	}
	public void con(){
		this.con(new Object[0]);
	}
	public void con(Object... parameterValues){
		Object o=null;
		try{
			o=this.cd.newInstance(parameterValues);
		}catch(Exception e){
			throw new InheritanceRuntimeException(e);
		}
		this.md=new MethodDeposit(o);
		this.md.accessPrivate(false);
		this.fd=new FieldDeposit(o);
		this.fd.accessPrivate(false);
	}
	public Object field(String name){
		if(null==this.fd) this.con();
		try{
			return this.fd.get(name);
		}catch(Exception e){
			if(e instanceof IllegalAccessException){
				throw new InheritanceRuntimeException(e);
			}else{
				for(Extends ex:this.sups){
					try{
						return ex.field(name);
					}catch(Exception e1){
						continue;
					}
				}
				throw new InheritanceRuntimeException(e);
			}
		}
	}
	public Object invoke(String name){
		return invoke(name,new Object[0]);
	}
	public Object invoke(String name, Object...parameterValues){
		if(null==this.md) this.con();
		try{
			return this.md.invoke(name, parameterValues);
		}catch(Throwable t){
			if(t instanceof IllegalAccessException){
				throw new InheritanceRuntimeException(t);
			}else{
				for(Extends ex:this.sups){
					try{
						return ex.invoke(name, parameterValues);
					}catch(Throwable t1){
						continue;
					}
				}
				throw new InheritanceRuntimeException(t);
			}
		}
	}
	public Extends sup(String name){
		for(Extends ex:this.sups){
			if(ex.c.getName().equals(name)){
				return ex;
			}
		}
		for(Extends ex:this.sups){
			if(ex.c.getSimpleName().equals(name)){
				return ex;
			}
		}
		throw new InheritanceRuntimeException("Super class not found.");
	}
	public Set<Class<?>> getInterfaces(){
		Set<Class<?>> set=new HashSet<Class<?>>();
		for(Extends ex:sups){
			set.addAll(ex.getInterfaces());
		}
		for(Class<?> cls:c.getInterfaces()){
			set.add(cls);
		}
		return set;
	}
	public Class<?> getC(){
		return this.c;
	}
	
}
