package cass.toolbox.ioc;

public class CircularDependencyError extends Error {
	/**
	 * 
	 */
	private static final long serialVersionUID = -874173024056198557L;
	
	private String bean=null;
	private Class<?> cls=null;
	public CircularDependencyError(Class<?> cls){
		super("occurred when constructing class"+cls);
		this.cls=cls;
	}
	public CircularDependencyError(String bean){
		super("occurred when constructing bean"+bean);
		this.bean=bean;
	}
	public String getBean(){
		return bean;
	}
	public Class<?> getCls(){
		return cls;
	}
}
