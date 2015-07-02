package cass.pure.persist.exception;

import cass.pure.persist.Entity;

public class EntityOperationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -661957169504757033L;

	public EntityOperationException(Entity e, String operation, Throwable t) {
		super("Cannot " + operation + " entity :" + e, t);
	}

	public EntityOperationException(String str) {
		super(str);
	}

	public EntityOperationException(String str, Throwable t) {
		super(str, t);
	}
}
