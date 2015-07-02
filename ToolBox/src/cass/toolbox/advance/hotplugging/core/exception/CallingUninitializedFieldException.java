package cass.toolbox.advance.hotplugging.core.exception;
/**
 * Thrown when calling an uninitialized field, usually because setters aren't used, or are misused.
 * @author wkgcass
 */
public class CallingUninitializedFieldException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3973383571137774200L;
	
	public CallingUninitializedFieldException(Object caller,String field){
		super(caller.getClass().getName()+'.'+field+" has not been initialized.");
	}
}
