package cass.pure.persist.exception;

public class TargetClosedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8737918621815954249L;

	public TargetClosedException(Object obj) {
		super(obj + " is closed");
	}
}
