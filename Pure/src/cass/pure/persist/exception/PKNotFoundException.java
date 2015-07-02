package cass.pure.persist.exception;

public class PKNotFoundException extends ParseException {

	private static final long serialVersionUID = 774575893504794463L;

	public PKNotFoundException(Class<?> entityClass) {
		super("Cannot infer primary key from entity: " + entityClass.getName());
	}

}
