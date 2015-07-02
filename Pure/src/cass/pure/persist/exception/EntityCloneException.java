package cass.pure.persist.exception;

import cass.pure.persist.Entity;

public class EntityCloneException extends EntityOperationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 219784537588679328L;

	public EntityCloneException(Entity e, Throwable t) {
		super("Cannot clone entity " + e, t);
	}
}
