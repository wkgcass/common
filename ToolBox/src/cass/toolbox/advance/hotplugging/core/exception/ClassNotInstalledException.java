package cass.toolbox.advance.hotplugging.core.exception;
/**
 * Thrown when trying to use an uninstalled class.
 * @author wkgcass
 */
public class ClassNotInstalledException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6889305709579227533L;

	public ClassNotInstalledException(String s){
		super(s);
	}
}
