package cass.toolbox.advance.hotplugging.core.exception;

/**
 * Thrown in the 'installing' step of updating a class.
 * @author wkgcass
 *
 */
public class UpdateUnsuccessfulException extends Exception {
	private static final long serialVersionUID = 7275446325782144254L;
	private Exception e;
	public UpdateUnsuccessfulException(String s,Exception e){
		super(s);
		this.e=e;
	}
	public Exception getTargetException(){
		return e;
	}
}
