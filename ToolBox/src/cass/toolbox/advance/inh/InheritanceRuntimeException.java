package cass.toolbox.advance.inh;

public class InheritanceRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1896272881762652212L;
	public InheritanceRuntimeException(){
		super();
	}
	public InheritanceRuntimeException(String s){
		super(s);
	}
	public InheritanceRuntimeException(Throwable t){
		super(t);
	}
	public InheritanceRuntimeException(String s, Throwable t){
		super(s,t);
	}
}
