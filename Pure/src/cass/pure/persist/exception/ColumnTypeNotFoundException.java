package cass.pure.persist.exception;

import java.lang.reflect.Field;

public class ColumnTypeNotFoundException extends ParseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1546963048526967852L;

	public ColumnTypeNotFoundException(Field field) {
		super("Cannot infer column type of field: " + field);
	}
}
