package cass.toolbox.ioc.annotations;

import cass.toolbox.ioc.AnnotationHandlingException;

public class WrongTargetTypeException extends AnnotationHandlingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8421366481986589705L;
	public WrongTargetTypeException(Class<?> input, Class<?> expecting){
		super("Expecting "+expecting+", got "+input);
	}
}
