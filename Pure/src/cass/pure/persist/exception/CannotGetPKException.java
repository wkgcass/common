package cass.pure.persist.exception;

public class CannotGetPKException extends ParseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8022559806043800105L;

	public CannotGetPKException(Exception e) {
		super(e);
	}
}
