package cass.toolbox.advance.hotplugging.core.exception;

/**
 * Thrown when dependency of a class cannot be found.
 * @author wkgcass
 *
 */
public class DependencyNotFoundException extends Exception {
	private static final long serialVersionUID = -2661020332744601498L;
	public DependencyNotFoundException(String s){
		super(s);
	}
}
