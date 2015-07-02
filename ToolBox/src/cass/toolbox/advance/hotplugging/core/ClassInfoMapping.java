package cass.toolbox.advance.hotplugging.core;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Maps class name to class file path
 * @author wkgcass
 * @version 0.0.1
 *
 */
public class ClassInfoMapping {
	
	protected Map<String, Set<String>> dependency=new HashMap<String,Set<String>>();
	protected List<String> dirs=new LinkedList<String>();
	
	protected List<String> doNotLoad=null;
	
	protected Map<String,String> jars=new HashMap<String,String>();
	
	protected  Map<String,String> map=new HashMap<String,String>();

	/**
	 * Add all jars and class files in this directory and sub directories of this directory.
	 * e.g. you have a directory : /Users/wkgcass/WorkSpace/Hotplugging/cass/hotplugging/core
	 * <br/> and a jar : /Users/wkgcass/WorkSpace/Hotplugging/hotplugging.jar
	 * @param dir e.g. File("/Users/wkgcass/WorkSpace/Hotplugging/cass/hotplugging")
	 * @param base e.g. cass/hotplugging
	 * @param depend e.g. /Users/wkgcass/WorkSpace/Hotplugging 
	 */
	protected void addBaseDir(File dir , String base, String depend){
		if(dir.isDirectory() && dir.canRead()){
			File[] underDir = dir.listFiles();
			for(File file : underDir){
				if(file.canRead()){
					if(file.isDirectory()){
						String base2=null;
						if(base.equals("")){
							base2=file.getName();
						}else{
							base2=base+File.separatorChar+file.getName();
						}
						this.addBaseDir(file , base2, depend);
					}else{
						String suffix=this.getSuffix(file);
						
						if(suffix.equalsIgnoreCase(".jar")){
							if(base.equals("")){
								this.addJar(file,depend ,depend);
							}else{
								this.addJar(file,depend+File.separatorChar+base ,depend);
							}
							continue;
						}
						
						if(suffix.equalsIgnoreCase(".class")){
							this.addClassPath(file.getName(), base,depend , depend);
							continue;
						}
					}
				}
			}
		}
	}
	
	public boolean addBaseDir(String dir){
		if(null==dir) return false;
		dir=dir.trim();
		if(dir.endsWith(File.separator)){
			dir=dir.substring(0,dir.length()-1);
		}
		if(this.dirs.contains(dir)){
			return true;
		}
		
		File d = new File(dir);
		if(d.canRead()){
			this.dependency.put(dir, new HashSet<String>());
			this.dirs.add(dir);
		}else{
			return false;
		}
		this.addBaseDir(d,"" ,dir);
		return true;
		
	}
	
	public void addClassPath(String name,String path){
		name=name.trim();
		path=path.trim();
		String nameDotClass=name.substring(name.lastIndexOf('.')+1)+".class";
		char div='/';
		if(path.contains("\\")) div='\\';
		String[] tmp=name.split("\\.");
		for(int i=0;i<tmp.length;++i){
			path=path.substring(0,path.lastIndexOf(div));
		}
		String base=null;
		if(name.contains(".")){
			base=name.substring(0,name.lastIndexOf('.'));
		}else{
			base="";
		}
		base=base.replace('.', div);
		this.addClassPath(nameDotClass, base, path, null);
	}
	
	/**
	 * e.g. you have a directory : /Users/wkgcass/WorkSpace/Hotplugging/cass/hotplugging/core
	 * <br/> and a jar : /Users/wkgcass/WorkSpace/Hotplugging/hotplugging.jar
	 * @param name e.g. ClassInfoMapping.class
	 * @param base e.g. cass/hotplugging/core
	 * @param path e.g. /Users/wkgcass/WorkSpace/Hotplugging/hotplugging.jar!
	 * <br/> or /Users/wkgcass/WorkSpace/Hotplugging
	 * @param depend e.g. hotplugging.jar
	 * <br/>or /User/wkgcass/WorkSpace/Hotplugging 
	 * <br/>(can be set to null)
	 */
	protected void addClassPath(String name, String base ,String path , String depend){
		if(path.contains("!")){
			if(base.equals("")){
				path=path+'/'+name;
			}else{
				path=path+'/'+base+'/'+name;
			}
		}else{
			if(base.equals("")){
				path=path+File.separatorChar+name;
			}else{
				path=path+File.separatorChar+base+File.separator+name;
			}
		}
		
		base=base.replace("/", ".");
		base=base.replace("\\", ".");
		
		if(!base.equals("")){
			name=base+'.'+name;
		}
		name=name.substring(0,name.length()-6);
		
		if(this.doNotLoad.contains(name)) return;
		
		this.map.put(name, path);
		if(null!=depend){
			this.dependency.get(depend).add(name);
		}
	}
	
	/**
	 * add all classes in the jar file.
	 * e.g. you have a directory : /Users/wkgcass/WorkSpace/Hotplugging/cass/hotplugging/core
	 * <br/> and a jar : /Users/wkgcass/WorkSpace/Hotplugging/hotplugging.jar
	 * @param jar e.g. new File("/Users/wkgcass/WorkSpace/Hotplugging/hotplugging.jar")
	 * @param path e.g. /User/wkgcass/WorkSpace/Hotplugging/
	 * @param depend e.g. /Users/wkgcass/WorkSpace/Hotplugging
	 * @return true if file can read and jar can be parsed
	 */
	protected boolean addJar(File jar ,  String path, String depend){
		if(this.jars.containsKey(jar.getName())) return true;
		if(jar.canRead() && jar.isFile()){
			String suffix=this.getSuffix(jar);
			if(suffix.equalsIgnoreCase(".jar")){
				//====
				this.jars.put(jar.getName(), path+File.separatorChar+jar.getName());
				try {
					JarFile jarFile=new JarFile(jar);
					Enumeration<JarEntry> entries=jarFile.entries();
					this.dependency.put(jar.getName(), new HashSet<String>());
					if(null!=depend){
						this.dependency.get(depend).add(jar.getName());
					}
					while(entries.hasMoreElements()){
						JarEntry entry=entries.nextElement();
						String name=entry.getName();
						String suffix2=this.getSuffix(name);
						if(suffix2.equalsIgnoreCase(".class")){
							String className=null;
							String classBase=null;
							if(name.contains("/")){
								className = name.substring(name.lastIndexOf('/')+1);
								classBase=name.substring(0,name.lastIndexOf('/'));
							}else{
								className=name;
								classBase="";
							}
							this.addClassPath(className, classBase, jar.getPath()+'!' , jar.getName());
							continue;
						}
					}
				} catch (Exception e) {
					return false;
				}
				//====
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	public boolean addJar(String jar, String path) {
		if(this.jars.containsKey(jar)){
			return true;
		}
		String div="/";
		if(path.contains("\\")) div="\\";
		String suffix=this.getSuffix(path);
		String path2=null;
		if(suffix.equalsIgnoreCase(".jar")){
			if(path.contains("/")
					||
					path.contains("\\")
					){
				path2=path.substring(0,path.lastIndexOf(div));
			}
			return this.addJar(new File(path), path2, null);
		}else{
			if(path.endsWith(div)){
				path2=path+jar;
			}else{
				path2=path+div+jar;
			}
			return this.addJar(new File(path2), path, null);
		}
	}
	public Set<String> getDependency(String name){
		name=name.trim();
		if(name.endsWith("\\")
				||
				name.endsWith("/")){
			name=name.substring(0,name.length()-1);
		}
		Set<String> ret=new HashSet<String>();
		Set<String> dep=this.dependency.get(name);
		if(null!=dep){
			ret.addAll(dep);
		}
		int sizeBefore = ret.size();
		int sizeAfter = 0;
		while(sizeBefore!=sizeAfter){
			sizeBefore=ret.size();
			for(String s : ret){
				dep=this.dependency.get(s);
				try{
					ret.addAll(dep);
				}catch(NullPointerException e){
					continue;
				}
			}
			sizeAfter=ret.size();
		}
		return ret;
	}
	
	public String getPath(String name){
		name=name.trim();
		return this.map.get(name);
	}
	
	protected String getSuffix(File file){
		if(file.isFile()){
			return this.getSuffix(file.getName());
		}else{
			return "";
		}
	}
	
	protected String getSuffix(String name){
		String suffix=null;
		if(name.contains(".")){
			suffix=name.substring(name.lastIndexOf('.'));
			if(suffix.contains(File.separator)) suffix="";
		}else{
			suffix="";
		}
		return suffix;
	}
	
	public boolean removeBaseDir(int dirIndex){
		if(this.dirs.size()<=dirIndex || dirIndex<0){
			return false;
		}
		return this.removeBaseDir(this.dirs.get(dirIndex));
	}
	
	public boolean removeBaseDir(String dir){
		if(this.dirs.contains(dir)){
			Set<String> set=this.getDependency(dir);
			for(String s:set){
				this.dirs.remove(s);
				this.jars.remove(s);
				this.map.remove(s);
				this.dependency.remove(s);
			}
			return true;
		}else{
			return false;
		}
	}
	
	public void setDoNotLoad(List<String> list){
		if(null==list) return;
		this.doNotLoad=list;
	}
	
	public void setPathAll(Map<String,String> map){
		for(String s:map.keySet()){
			if(null==s || null==map.get(s)) continue;
			this.addClassPath(s, map.get(s));
		}
	}
}
