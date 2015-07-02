package cass.pure.persist;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cass.pure.persist.exception.EntityCloneException;
import cass.pure.persist.exception.EntityOperationException;
import cass.pure.persist.exception.ParseException;

/**
 * The class representing an Entity, which provides methods like save, refresh,
 * remove<br/>
 * it should be the super class of all entities.
 * 
 * @author wkgcass
 *
 */
public abstract class Entity {

	private static Logger logger = Logger.getLogger(Entity.class);

	/**
	 * Free entity
	 */
	public static final int FREE = 0;
	/**
	 * An entity with uncommitted changes
	 */
	public static final int TRANSITION = 1;
	/**
	 * An entity has been removed but not committed
	 */
	public static final int DYING = -1;
	/**
	 * An entity already saved in database
	 */
	public static final int PERSIST = 2;

	/**
	 * a set containing corresponding transactions.
	 */
	private Set<Transaction> transactionSet = new HashSet<Transaction>();
	/**
	 * persist level of this entity
	 */
	int persistLevel = 0;

	protected Entity() {
	}

	/**
	 * add a transaction to this entity
	 * 
	 * @param t
	 */
	void addTransaction(Transaction t) {
		transactionSet.add(t);
		t.registerEntity(this);
	}

	@Override
	protected final Entity clone() {
		try {
			Constructor<?> con = EntityManager.getRealEntityClass(this)
					.getDeclaredConstructor();
			con.setAccessible(true);
			Entity ret = (Entity) con.newInstance();
			for (Field f : SQLParser.getFields(this.getClass())) {
				f.setAccessible(true);
				f.set(ret, f.get(this));
			}
			ret.persistLevel = this.persistLevel;
			ret.transactionSet = this.transactionSet;
			return ret;
		} catch (Exception e) {
			throw new EntityCloneException(this, e);
		}
	}

	@Override
	public final boolean equals(Object o) {
		if (null == o)
			return false;
		if (this == o)
			return true;
		Class<?> cls = EntityManager.getRealEntityClass(this);
		if (cls.isAssignableFrom(o.getClass())) {
			Field[] fields = SQLParser.getFields(cls);
			for (Field f : fields) {
				f.setAccessible(true);
				try {
					Object tmp = f.get(this);
					Object that = f.get(o);
					if ((tmp == null ? that != null : !tmp.equals(that))) {
						return false;
					}
				} catch (Exception e) {
					logger.error("Error occured while comparing entities", e);
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * add an update sql to the transaction.
	 * 
	 * @param saveTrueOrRemoveFalse
	 *            true if the action is to save, false to remove
	 * @throws Throwable
	 */
	private void executeUpdate(boolean saveTrueOrRemoveFalse) throws Throwable {
		Long index;
		Iterator<Transaction> it = transactionSet.iterator();
		Transaction t = it.next();
		if (saveTrueOrRemoveFalse) {
			index = t.executeSave(this);
		} else {
			index = t.executeRemove(this);
		}
		while (it.hasNext()) {
			it.next().executeUpdate(index);
		}
	}

	/**
	 * determines whether this entity has been modified.
	 * 
	 * @return true if the entity has been modified, false otherwise.
	 */
	public final boolean isModified() {
		return EntityManager.beforeCommit.containsKey(this) ? !this
				.equals(EntityManager.beforeCommit.get(this)) : false;
	}

	/**
	 * the persist level of the entity.<br/>
	 * There are 4 levels ,<br/>
	 * <ul>
	 * <li>FREE - 0 - the entity has no relation to the database.</li>
	 * <li>TRANSITION - 1 - the entity has uncommitted changes.( after
	 * committing them, it would be transformed into level PERSIST</li>
	 * <li>PERSIST - 2 - the entity is currently have same value to the
	 * database.</li>
	 * <li>DYING - -1 - the entity is to be removed, but the action not yet
	 * committed.</li>
	 * 
	 * @return integers representing FREE, TRANSITION, PERSIST or DYING
	 */
	public final int persistLevel() {
		return persistLevel;
	}

	/**
	 * refresh the persist entity.<br/>
	 * all fields will correspond to up-to-date database values.<br/>
	 * if the entity is not in persist level, nothing would be done.
	 */
	public final void refresh() {
		if (persistLevel == PERSIST) {
			try {
				EntityManager.refreshEntity(this);
			} catch (Throwable t) {
				throw new EntityOperationException(this, "refresh", t);
			}
		} else {
			throw new EntityOperationException(
					"Cannot refresh a non-PERSIST entity");
		}
	}

	/**
	 * remove a transaction to this entity
	 * 
	 * @param t
	 */
	void releaseTransaction(Transaction t) {
		transactionSet.remove(t);
	}

	/**
	 * remove the entity.<br/>
	 * if this is a free or dying entity, an exception would be thrown.
	 */
	public final void remove() {
		if (persistLevel == FREE) {
			throw new EntityOperationException("Cannot remove a FREE entity");
		}
		if (persistLevel == DYING) {
			throw new EntityOperationException("Cannot remove a DYING entity");
		}
		try {
			executeUpdate(false);
			persistLevel = TRANSITION;
		} catch (Throwable e) {
			throw new EntityOperationException(this, "remove", e);
		}
	}

	/**
	 * save a free entity.<br/>
	 * if the entity is not a free entity, then this method will do nothing.
	 */
	public final void save() {
		try {
			if (persistLevel == FREE) {
				this.transactionSet.add(PersistController.getTransaction());
				executeUpdate(true);
				persistLevel = TRANSITION;
			}
		} catch (Throwable t) {
			throw new EntityOperationException(this, "save", t);
		}
	}

	public final String toString() {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("{");
			Field[] fields = SQLParser.getFields(EntityManager
					.getRealEntityClass(this));
			for (Field f : fields) {
				Class<?> type = f.getType();
				if (Collection.class.isAssignableFrom(type)
						|| Map.class.isAssignableFrom(type)) {
					continue;
				}
				if (!sb.toString().equals("{")) {
					sb.append(", ");
				}
				f.setAccessible(true);
				Object value = f.get(this);
				if (null == value) {
					sb.append("\"").append(SQLParser.getColumnName(f))
							.append("\":null");
				} else {
					if (Entity.class.isAssignableFrom(type)) {
						SQLParser p = SQLParser.getParser(type);
						Field pk = SQLParser.getPK(type);
						pk.setAccessible(true);
						Object pkValue = pk.get(value);
						String str = p.toSQLValue(pkValue);
						sb.append("\"").append(SQLParser.getColumnName(f))
								.append("\":").append("\"").append(str)
								.append("\"");
					} else {
						SQLParser p = SQLParser.getParser(EntityManager
								.getRealEntityClass(this));
						String str = p.toSQLValue(value);
						sb.append("\"").append(SQLParser.getColumnName(f))
								.append("\":").append("\"").append(str)
								.append("\"");
					}
				}
			}
			sb.append("}");
			return sb.toString();
		} catch (Throwable t) {
			throw new ParseException(t);
		}
	}
}
