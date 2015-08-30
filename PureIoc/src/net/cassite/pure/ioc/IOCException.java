package net.cassite.pure.ioc;
/**
 * thrown by cass.toolbox.ioc
 * @author wkgcass
 *
 */
public class IOCException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4202618483071924377L;
	public IOCException(){
		super();
	}
	public IOCException(String s){
		super(s);
	}
	public IOCException(Throwable t){
		super(t);
	}
	public IOCException(String s,Throwable t){
		super(s,t);
	}
}
