package cass.pure.persist.exception;

public class EntityInstantiateException extends EntityOperationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4078904223372046872L;

	public EntityInstantiateException(Class<?> entityClass, Throwable e) {
		super("Cannot instantiate " + entityClass, e);
	}
}
