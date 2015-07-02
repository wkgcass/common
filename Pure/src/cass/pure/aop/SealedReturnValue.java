package cass.pure.aop;

public class SealedReturnValue extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 860191386064014858L;
	private Object retVal;

	public SealedReturnValue(Object retVal) {
		this.retVal = retVal;
	}

	public Object getReturnValue() {
		return this.retVal;
	}
}
