package cass.pure.ioc;

public class ConstructingMultiSingletonException extends IOCException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 461390673601453091L;
	public ConstructingMultiSingletonException(Class<?> singleton){
		super(singleton+" is marked as Singleton and has already instantiated");
	}
}
