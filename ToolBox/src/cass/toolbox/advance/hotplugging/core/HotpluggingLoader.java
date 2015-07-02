package cass.toolbox.advance.hotplugging.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cass.toolbox.advance.hotplugging.core.exception.FileInsideJarNotFoundException;

/**
 * 
 * @author wkgcass
 * @version 0.0.1
 *
 */
public class HotpluggingLoader extends ClassLoader {
	
	private ClassLoaderManager clm=null;
	private String name;
	
	/**
	 * A HotpluggingLoader only load one class.
	 * @param name name of class related to this loader.
	 * @param clm manager the loader relates to.
	 * @throws Exception
	 * <br/><ul><b>FileInsideJarNotFoundException</b>, thrown when class file not found inside a jar file.
	 * <br/>and other java.io related exceptions</ul>
	 */
	public HotpluggingLoader(String name, ClassLoaderManager clm) throws Exception{
		super(null);
		this.name=name;
		this.clm=clm;
		this.load(name);
	}
	
	protected byte[] loadDirectly(String name) throws Exception{
		byte[] raw;
		String path=this.clm.getClassInfoMapping().getPath(name);
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
					try{
						fin=jarfile.getInputStream(entry);
					}catch(Exception e){
						jarfile.close();
						throw e;
					}
					length=entry.getSize();
					break;
				}
			}
			if(null==fin) throw new FileInsideJarNotFoundException(path);
			try{
				raw=this.readStreamIntoArray(fin, length);
			}catch(Exception e){
				fin.close();
				jarfile.close();
				throw e;
			}
			jarfile.close();
		}else{
			//普通文件
			File f=new File(path);
			InputStream fin=new FileInputStream(f);
			try{
				raw=this.readStreamIntoArray(fin, f.length());
			}catch(Exception e){
				fin.close();
				throw e;
			}
			fin.close();
		}
		return raw;
	}

	private Class<?> load(String name) throws Exception{
		byte[] raw=null;
		
		String path=this.clm.getClassInfoMapping().getPath(name);
		
		if(null==path){
			//无配置路径，转给manager
			if(ClassLoaderManager.NOT_INSTALLED!=this.clm.isInstalled(name)){
				return this.clm.getClass(name);
			}else{
				this.clm.install(name);
				return this.clm.getClass(name);
			}
		}else{
			//在这里加载
			raw=this.loadDirectly(name);
		}
	
		return defineClass(name,raw,0,raw.length);
		
	}
	
	@Override
	protected Class<?> loadClass(String name, boolean resolve)  throws ClassNotFoundException { 
		// First, check if the class has already been loaded
		Class<?> c = findLoadedClass(name);
		if (c == null) {//not found
			try {
				if(ClassLoaderManager.NOT_INSTALLED!=this.clm.isInstalled(name)){
					c = this.clm.getClass( 
							resolve?null:this.name
									,name);
				}else{
					this.clm.install(
							resolve?null:this.name
									,name);
					c = this.clm.getClass(
							resolve?null:this.name
									,name);
				}
			} catch (Exception e) {
				c = null;
			}
		}
		if(c == null) throw new ClassNotFoundException();
		return c;
	}
	
	protected byte[] readStreamIntoArray(InputStream fin,long len) throws Exception {
		if(null==this.clm.getDecoder()){
	        byte[] raw = new byte[(int) len]; 
			fin.read(raw);
			fin.close();  
			return raw;
		}else{
			return this.clm.getDecoder().decode(fin, len);
		}
    }

}
