package cass.toolbox.advance.hotplugging.run;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cass.toolbox.advance.hotplugging.core.exception.FileInsideJarNotFoundException;

public class InitLoader extends ClassLoader {
	
	private Object mapping=null;
	private Object clm=null;
	
	protected Class<?> load(String name) throws Exception{
		byte[] raw;
		String path=null;
		if(name.equals("cass.hotplugging.core.ClassInfoMapping")){
			path=this.getClass().getResource("/").toString()+"cass/hotplugging/core/ClassInfoMapping.class";
			path=path.replace("%20", " ").substring(6);
		}else{
			if(null!=this.mapping){
				Method m=mapping.getClass().getMethod("getPath", String.class);
				path=(String)m.invoke(mapping, name);
			}
		}
		
		if(null==path){
			//无配置路径，转system加载
			return ClassLoader.getSystemClassLoader().loadClass(name);
		}else{
			//在这里加载
			if(path.contains(".jar!")){
				//是jar中的文件
				JarFile jarfile=new JarFile(path.substring(0, path.indexOf(".jar!")+4));
				String inJar = path.substring(path.indexOf(".jar!")+5);
				if(inJar.startsWith("/")) inJar=inJar.substring(1);
				Enumeration<JarEntry> entries = jarfile.entries();
				InputStream fin=null;
				long length=0;
				while(entries.hasMoreElements()){
					JarEntry entry=entries.nextElement();
					if(entry.getName().equals(inJar)){
						fin=jarfile.getInputStream(entry);
						length=entry.getSize();
						break;
					}
				}
				if(null==fin) throw new FileInsideJarNotFoundException(path);
				raw=this.readStreamIntoArray(fin, length);
				jarfile.close();
			}else{
				//普通文件
				File f=new File(path);
				InputStream fin=new FileInputStream(f);
				raw=this.readStreamIntoArray(fin, f.length());
			}
		}
		
		try{
		return defineClass(name,raw,0,raw.length);
		}catch(ClassFormatError e){
			throw e;
		}
	}
	
	protected byte[] readStreamIntoArray(InputStream fin,long len) throws Exception {
        byte[] raw = new byte[(int) len]; 
		fin.read(raw);
		fin.close();  
		return raw;
    }
	
	public Class<?> loadClass(String name,boolean resolve)  throws ClassNotFoundException {
		Class<?> c = findLoadedClass(name);
		if(null==c){
			if(name.startsWith("java.")){
	    		return ClassLoader.getSystemClassLoader().loadClass(name);
	    	}
		}
		
		if(null==this.clm){
			// First, check if the class has already been loaded
			if (c == null) {
			    try {
			    	c=this.load(name);
			    }catch(Exception e){
			    	throw new ClassNotFoundException(name);
			    }
			}
			if (resolve) {
			    resolveClass(c);
			}
			return c;
		}else{
			c=null;
			try {
				Method m = this.clm.getClass().getMethod("isInstalled", String.class);
				Object o = m.invoke(this.clm, name);
				if(!o.equals(2)){
					Method n = this.clm.getClass().getMethod("getClass", String.class);
					c=(Class<?>)n.invoke(this.clm, name);
				}else{
					Method p =this.clm.getClass().getMethod("install", String.class);
					p.invoke(this.clm, name);
					Method n = this.clm.getClass().getMethod("getClass", String.class);
					c=(Class<?>)n.invoke(this.clm, name);
				}
			} catch (Exception e) {
				c=null;
			}
			if(null==c) throw new ClassNotFoundException(name);
			else return c;
		}
	}

	public void setClassInfoMapping(Object mapping) {
		this.mapping=mapping;
	}

	public void setManager(Object clm) {
		this.clm=clm;
	}
	
}
