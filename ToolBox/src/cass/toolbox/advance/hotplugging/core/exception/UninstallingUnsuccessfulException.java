package cass.toolbox.advance.hotplugging.core.exception;

/**
 * Thrown when uninstalling a class that has not been installed, or the class was registered.
 * @author wkgcass
 *
 */
public class UninstallingUnsuccessfulException extends Exception {
	private static final long serialVersionUID = 8897157029120804185L;
	public UninstallingUnsuccessfulException(String s){
		super(s);
	}
}
