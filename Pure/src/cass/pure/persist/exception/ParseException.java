package cass.pure.persist.exception;

public class ParseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 829672947424532371L;

	public ParseException() {
		super();
	}

	public ParseException(String str) {
		super(str);
	}

	public ParseException(String str, Throwable t) {
		super(str, t);
	}

	public ParseException(Throwable t) {
		super(t);
	}
}
