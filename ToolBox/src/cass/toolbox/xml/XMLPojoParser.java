package cass.toolbox.xml;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;

import cass.toolbox.reflect.Reflection;

public class XMLPojoParser {
	private XMLParser parser;
	
	public XMLPojoParser(File file) throws Exception{
		this.parser=new XMLParser(file);
	}
	public XMLPojoParser(XMLParser parser){
		this.parser=parser;
	}
	
	@SuppressWarnings("unchecked")
	public <P> P getPojo(String position, Class<P> pclass) throws Exception{
		P p=pclass.newInstance();
		Object o=parser.select(position);
		if(o instanceof Map<?,?>){
			Map<String,?> map=(Map<String,?>)o;
			Method[] ms=pclass.getMethods();
			for(Method m : ms){
				if(!Reflection.isSetter(m)) continue;
				String name=m.getName().substring(3).toLowerCase();
				if(map.containsKey(name)){
					m.invoke(p, map.get(name));
				}
			}
		}else{
			throw new IllegalArgumentException("position : "+position);
		}
		return p;
	}
}
