package cass.pure.ioc.initsupport;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cass.pure.init.InitializeSupport;
import cass.pure.ioc.IOCController;
import cass.pure.ioc.annotations.Variable;

public class VariableInitSupport implements InitializeSupport {
	
	private Map<String,Member> vars=new HashMap<String,Member>();

	@Override
	public void addClass(Class<?> cls) {
		for(Method m: cls.getMethods()){
			Variable v=m.getAnnotation(Variable.class);
			if(null==v) continue;
			vars.put(v.name(), m);
		}
		
		for(Field f:cls.getFields()){
			Variable v=f.getAnnotation(Variable.class);
			if(null==v) continue;
			vars.put(v.name(), f);
		}
	}

	@Override
	public void doInit(String[] args) {
		for(String name : vars.keySet()){
			IOCController.registerVariable(name, vars.get(name));
		}
	}

	@Override
	public double priority() {
		return -2.24;
	}

}
