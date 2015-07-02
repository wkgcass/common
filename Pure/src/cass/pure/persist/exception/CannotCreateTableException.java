package cass.pure.persist.exception;

public class CannotCreateTableException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6733005712741664891L;

	public CannotCreateTableException(Class<?> entityClass) {
		super("Cannot create table with class: " + entityClass);
	}

	public CannotCreateTableException(Class<?> entityClass, Throwable t) {
		super("Cannot create table with class: " + entityClass, t);
	}
}
