package cass.pure.persist.exception;

import cass.pure.persist.Entity;

public class EntityParseException extends ParseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1351584830288791970L;

	public EntityParseException(Entity entity, Throwable t) {
		super("Cannot parse " + entity, t);
	}

	public EntityParseException(Entity entity, String str) {
		super("Cannot parse " + entity + " because " + str);
	}
}
