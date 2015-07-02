package cass.toolbox.advance.hotplugging.core.exception;

/**
 * Thrown when file cannot be found inside a jar file.
 * @author wkgcass
 *
 */
public class FileInsideJarNotFoundException extends Exception {
	private static final long serialVersionUID = -3010125675315826891L;
	public FileInsideJarNotFoundException(String s){
		super(s);
	}
}
