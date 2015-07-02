package cass.toolbox.advance.hotplugging.run;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;

public class LoopLoader extends ClassLoader {
	private ClassLoader looploader=null;
	
	private Class<?> load(String name , boolean resolve) throws Exception{
		
			
			if(name.equals("cass.hotplugging.run.InitLoader")){
				byte[] raw;
				String path=this.getClass().getResource("InitLoader.class").toString();
				path=path.replace("%20", " ").substring(6);
				//在这里加载
				if(path.contains(".jar!")){
					//是jar中的文件
					JarURLConnection juc=(JarURLConnection)(new URL(path).openConnection());
					InputStream fin=juc.getInputStream();
					raw=this.readStreamIntoArray(fin, juc.getContentLength());
				}else{
					//普通文件
					File f=new File(path);
					InputStream fin=new FileInputStream(f);
					raw=this.readStreamIntoArray(fin, f.length());
				}
				
				return defineClass(name,raw,0,raw.length);
				
			}else{
				if(null==looploader
						||
						name.startsWith("java.")
				){
					return ClassLoader.getSystemClassLoader().loadClass(name);
				}else{
					Method loadClass = this.looploader.getClass().getDeclaredMethod("loadClass", String.class, boolean.class);
					loadClass.setAccessible(true);
					Class<?> cls = (Class<?>)loadClass.invoke(looploader, name , resolve);
					loadClass.setAccessible(false);
					return cls;
				}
			}
		
		
	}
	
	private byte[] readStreamIntoArray(InputStream fin,long len) throws Exception {
        byte[] raw = new byte[(int) len]; 
		fin.read(raw);
		fin.close();  
		return raw;
    }
	
	@Override
	public Class<?> loadClass(String name,boolean resolve) throws ClassNotFoundException{
		
		try {
			return this.load(name, resolve);
		} catch (Exception e) {
			if(e instanceof java.lang.reflect.InvocationTargetException){
				((java.lang.reflect.InvocationTargetException)e).getTargetException().printStackTrace();
			}
			throw new ClassNotFoundException(name);
		}
	}
	
	public void setLoop(ClassLoader cl){
		this.looploader=cl;
	}
}
