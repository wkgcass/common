package cass.pure.persist.support;

/**
 * tag an entity with an identifier
 * 
 * @author wkgcass
 *
 */
public class RecordIdentifier {
	/**
	 * class of the entity
	 */
	public Class<?> entityClass;
	/**
	 * primary key value
	 */
	public Object pk;

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (this == o)
			return true;
		if (o instanceof RecordIdentifier) {
			RecordIdentifier ri = (RecordIdentifier) o;
			if (entityClass == ri.entityClass && pk.equals(ri.pk)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return entityClass.hashCode() * 10 + pk.hashCode();
	}
}
