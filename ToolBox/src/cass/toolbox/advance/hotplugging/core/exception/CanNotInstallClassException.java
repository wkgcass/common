package cass.toolbox.advance.hotplugging.core.exception;
/**
 * Thrown when a .class file cannot be loaded.
 * @author wkgcass
 */
public class CanNotInstallClassException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1654378837232542805L;

	public CanNotInstallClassException(String s){
		super(s);
	}
}
