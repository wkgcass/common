package cass.pure.persist.exception;

public class CannotConnectException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 688006723508518194L;

	public CannotConnectException(String str) {
		super(str);
	}

	public CannotConnectException(Throwable t) {
		super(t);
	}
}
