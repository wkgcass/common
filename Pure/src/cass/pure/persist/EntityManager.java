package cass.pure.persist;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cass.pure.persist.annotation.DB;
import cass.pure.persist.annotation.Maps;
import cass.pure.persist.exception.CannotConnectException;
import cass.pure.persist.exception.EntityInstantiateException;
import cass.pure.persist.exception.QueryException;
import cass.pure.persist.pool.Properties;
import cass.pure.persist.support.LazyPersistenceList;
import cass.pure.persist.support.LazyPersistenceMap;
import cass.pure.persist.support.RecordIdentifier;

/**
 * manages all entities
 * 
 * @author wkgcass
 *
 */
public class EntityManager {

	/**
	 * create tables.<br/>
	 * this method automatically detects whether the table has already been
	 * created.
	 * 
	 * @throws Throwable
	 */
	public static void createTables() throws Throwable {
		logger.info("Creating tables...");
		Transaction transaction = PersistController.getTransaction();
		try {
			out: for (Class<?> entityClass : entityClassSet) {
				Connection conn = PersistController.getConnection(entityClass);
				List<Map<String, String>> tables = PersistController
						.executeQuery(conn, "show tables;");
				for (Map<String, String> map : tables) {
					if (map.containsValue(SQLParser.getTableName(entityClass))) {
						logger.info("Table "
								+ SQLParser.getTableName(entityClass)
								+ " has already been created");
						continue out;
					}
				}
				PersistController.closeConnection(conn);

				logger.info("Creating table "
						+ SQLParser.getTableName(entityClass));
				SQLParser parser = SQLParser.getParser(entityClass);
				String sql = parser.buildTableCreationSql(entityClass);
				logger.debug("Generated SQL String : " + sql);
				transaction.executeUpdate(entityClass, sql);
			}
		} catch (RuntimeException e) {
			logger.error("Exception occured during table creation", e);
			transaction.rollback();
		}
		transaction.commit();
		transaction.close();
	}

	public static <T extends Entity> List<T> findAll(Class<T> entityClass) {
		return findByWhereClause(entityClass, "");
	}

	public static <T extends Entity> List<T> findAll(Class<T> entityClass,
			T sample) {
		StringBuffer sb = new StringBuffer();
		try {
			SQLParser.whereClause(sample, sb);
		} catch (Throwable t) {
			throw new QueryException(t);
		}
		return findByWhereClause(entityClass, sb.toString());
	}

	@SuppressWarnings("unchecked")
	public static <T extends Entity> T findByPK(Class<T> entityClass,
			Serializable pk) {
		try {
			return (T) getEntity(entityClass, SQLParser.getParser(entityClass)
					.toSQLValue(pk));
		} catch (Throwable e) {
			throw new QueryException(e);
		}
	}

	public static <T extends Entity> List<T> findByWhereClause(
			Class<T> entityClass, String where) {
		try {
			Connection conn = PersistController.getConnection(entityClass);
			String sql = SQLParser.selectPart(entityClass).toString()+where;

			logger.debug("Generated SQL String : " + sql);

			List<Map<String, String>> results = PersistController.executeQuery(
					conn, sql);
			PersistController.closeConnection(conn);
			List<String> pkValueList = new ArrayList<String>();
			for (Map<String, String> map : results) {
				pkValueList.add(map.get(SQLParser.getTable_Column(SQLParser
						.getPK(entityClass))));
			}
			return new LazyPersistenceList<T>(pkValueList, entityClass);
		} catch (Throwable e) {
			throw new QueryException(e);
		}
	}

	/**
	 * get connection properties
	 * 
	 * @param entityClass
	 *            class of entity
	 * @return retrieved properties
	 * @see Properties
	 */
	public static Properties getConnProperties(Class<?> entityClass) {
		entityClass = getRealEntityClass(entityClass);
		if (entityClass.isAnnotationPresent(DB.class)) {
			DB db = entityClass.getAnnotation(DB.class);
			Properties p = new Properties();
			p.url = db.url();
			p.user = db.user();
			p.pwd = db.pwd();
			return p;
		} else {
			Package pkg = entityClass.getPackage();
			if (pkg.isAnnotationPresent(DB.class)) {
				DB db = pkg.getAnnotation(DB.class);
				Properties p = new Properties();
				p.url = db.url();
				p.user = db.user();
				p.pwd = db.pwd();
				return p;
			}
		}
		throw new CannotConnectException("Connection config not found");
	}

	/**
	 * retrieve or generate an entity.<br/>
	 * if the entity's identifier cannot be found, then generate the entity and
	 * put it into the <code>entities</code> map<br/>
	 * otherwise simply retrieve the existing record from the map. which means
	 * the system only has one instance of each identifier.
	 * 
	 * @param entityClass
	 *            class of the requiring entity
	 * @param pkValueString
	 *            string of primary key value retrieved from db.
	 * @return entity
	 */
	@SuppressWarnings({ "unchecked" })
	public static <T extends Entity> T getEntity(Class<?> entityClass,
			String pkValueString) {
		entityClass = getRealEntityClass(entityClass);
		SQLParser parser = SQLParser.getParser(entityClass);
		try {
			Field pkField = SQLParser.getPK(entityClass);
			Class<?> pkType = pkField.getType();
			Object pkValue = parser.toObject(pkType, pkValueString);
			Object entity = getEntityFromMap(pkValue, entityClass);
			if (entity == null) {
				Constructor<?> con = entityClass.getDeclaredConstructor();
				con.setAccessible(true);
				entity = con.newInstance();
				refreshEntity(entityClass, entity, pkValueString, parser);
				putEntityIntoMap(entityClass, pkValue, entity);
				T tmp = (T) entity;
				tmp.persistLevel = Entity.PERSIST;
				beforeCommit.put(tmp, tmp.clone());
			}
			T ret = (T) entity;
			ret.addTransaction(PersistController.getTransaction());
			return ret;
		} catch (Throwable e) {
			throw new EntityInstantiateException(entityClass, e);
		}
	}

	/**
	 * retrieve an entity from <code>entities</code> map
	 * 
	 * @param entityClass
	 *            class of the entity
	 * @param pk
	 *            primary key value ( java object)
	 * @return retrieved entity or null if not exist
	 */
	private static Entity getEntityFromMap(Object pk, Class<?> entityClass) {
		entityClass = getRealEntityClass(entityClass);
		RecordIdentifier ri = new RecordIdentifier();
		ri.entityClass = entityClass;
		ri.pk = pk;
		return entities.get(ri);
	}

	/**
	 * get real class of given entity class. in case using proxy objects.
	 * 
	 * @param entityClass
	 * @return the class of the entity which directly extends Entity
	 */
	public static Class<?> getRealEntityClass(Class<?> entityClass) {
		while (entityClass.getName().contains("$")) {
			entityClass = entityClass.getSuperclass();
		}
		return entityClass;
	}

	/**
	 * get real class of given entity. simply invoke
	 * {@link #getRealEntityClass(Class)} with entity.getClass() as arg0
	 * 
	 * @param entity
	 * @return the class of the entity which directly extends Entity
	 */
	public static Class<?> getRealEntityClass(Entity entity) {
		return getRealEntityClass(entity.getClass());
	}

	/**
	 * put the entity into the <code>entities</code> map
	 * 
	 * @param entityClass
	 *            class of the entity
	 * @param pk
	 *            primary key
	 * @param entity
	 *            entity to put
	 * @return original entity with same identifier, usually returns null
	 */
	private static Entity putEntityIntoMap(Class<?> entityClass, Object pk,
			Object entity) {
		entityClass = getRealEntityClass(entityClass);
		RecordIdentifier ri = new RecordIdentifier();
		ri.entityClass = entityClass;
		ri.pk = pk;
		Entity e = (Entity) entity;
		return entities.put(ri, e);
	}

	/**
	 * refresh an entity to up-to-date values
	 * 
	 * @param entityClass
	 * @param entity
	 * @param pkValueString
	 * @param parser
	 * @throws Throwable
	 */
	private static void refreshEntity(Class<?> entityClass, Object entity,
			String pkValueString, SQLParser parser) throws Throwable {
		Field[] fields = SQLParser.getFields(entityClass);

		List<Map<String, String>> resultList = SQLParser.getResultListByPK(
				entityClass, pkValueString);
		Map<String, String> result = resultList.get(0);

		for (Field f : fields) {
			Class<?> type = f.getType();
			Object value = null;
			if (Collection.class.isAssignableFrom(type)) {
				List<String> pks = SQLParser.getCascadingPKValueStringList(f,
						resultList);
				Class<?> targetClass = SQLParser.getGenericClass(f);
				value = new LazyPersistenceList<Entity>(pks, targetClass);
			} else if (Map.class.isAssignableFrom(type)) {
				List<String> pks = SQLParser.getCascadingPKValueStringList(f,
						resultList);
				Maps m = f.getAnnotation(Maps.class);
				Class<?> targetClass = m.ref();
				List<Entity> backedList = new LazyPersistenceList<Entity>(pks,
						targetClass);
				Field targetField = targetClass.getDeclaredField(m.target());
				Field valueField = targetClass.getDeclaredField(m.value());
				value = new LazyPersistenceMap(targetField, valueField,
						backedList);
			} else if (Entity.class.isAssignableFrom(type)) {
				String targetPKValueString = result.get(SQLParser
						.getTable_Column(f));
				value = getEntity(type, targetPKValueString);
			} else {
				if (f.getType().isPrimitive()
						&& result.get(SQLParser.getTable_Column(f)) == null) {
					if (f.getType() == int.class) {
						value = 0;
					} else if (f.getType() == double.class) {
						value = 0.0;
					} else if (f.getType() == float.class) {
						value = 0.0f;
					} else if (f.getType() == char.class) {
						value = 0;
					} else if (f.getType() == boolean.class) {
						value = false;
					} else if (f.getType() == long.class) {
						value = 0l;
					} else if (f.getType() == byte.class) {
						value = 0;
					} else {
						value = (short) 0;
					}
				} else {
					value = parser.toObject(type,
							result.get(SQLParser.getTable_Column(f)));
				}
			}
			f.setAccessible(true);
			f.set(entity, value);
		}
	}

	/**
	 * refresh an entity to up-to-date values<br/>
	 * calls {@link #refreshEntity(Class, Object, String, SQLParser)}
	 * 
	 * @param entity
	 *            the entity to be refreshed
	 * @throws Throwable
	 */
	static void refreshEntity(Entity entity) throws Throwable {
		Class<?> entityClass = getRealEntityClass(entity);
		SQLParser parser = SQLParser.getParser(entityClass);
		Field pk = SQLParser.getPK(entityClass);
		String pkValueString = parser.toSQLValue(pk.get(entity));
		refreshEntity(entityClass, entity, pkValueString, parser);
	}

	/**
	 * register an entity
	 * 
	 * @param entityClass
	 *            class of entity
	 */
	public static void register(Class<?> entityClass) {
		entityClass = getRealEntityClass(entityClass);
		if (Entity.class.isAssignableFrom(entityClass)) {
			entityClassSet.add(entityClass);
		}
	}

	/**
	 * remove all entities contained in the collection<br/>
	 * simply invoke {@link #removeEntity(Entity)} for each Entity in
	 * <code>entities</code>
	 * 
	 * @param entities
	 *            entities to remove
	 */
	static void removeEntity(Collection<Entity> entities) {
		for (Entity e : entities) {
			removeEntity(e);
		}
	}

	/**
	 * remove an entity, usually called when committing a <code>delete</code>
	 * operation
	 * 
	 * @param entity
	 *            the entity to remove
	 */
	static void removeEntity(Entity entity) {
		entities.values().remove(entity);
	}

	/**
	 * reset auto increment field value. <br/>
	 * if the field type is primitive, then let it be 0,<br/>
	 * otherwise null would be set
	 * 
	 * @param entity
	 *            entity to be reset
	 * @throws Exception
	 */
	static void resetAIPK(Entity entity) throws Exception {
		logger.debug("Reset auto increment field of " + entity);
		Field field = SQLParser.getPK(entity.getClass());
		field.setAccessible(false);
		if (SQLParser.isAutoIncrement(field)) {
			field.setAccessible(true);
			if (field.getType().isPrimitive()) {
				field.set(entity, 0);
			} else {
				field.set(entity, null);
			}
		}
	}

	/**
	 * reset an entity to its original state, usually called when rolling back
	 * 
	 * @param entity
	 *            the entity to reset
	 * @throws Exception
	 */
	static void resetEntity(Entity entity) throws Exception {
		logger.debug("Reset Entity " + entity);
		Entity origin = beforeCommit.get(entity);
		for (Field f : SQLParser.getFields(getRealEntityClass(entity))) {
			f.setAccessible(true);
			f.set(entity, f.get(origin));
		}
	}

	/**
	 * set auto increment field value.<br/>
	 * the value would be retrieved from a connection instance.
	 * 
	 * @param conn
	 *            connection which did the saving sql
	 * @param entity
	 *            entity to be set
	 * @throws Exception
	 */
	static void setAIPK(Connection conn, Entity entity) throws Exception {
		logger.debug("Set auto increment field of " + entity);
		Field field = SQLParser.getPK(entity.getClass());
		if (SQLParser.isAutoIncrement(field)) {
			SQLParser parser = SQLParser.getParser(entity.getClass());
			int i = parser.getLastGeneratedValue(conn);
			field.setAccessible(true);
			field.set(entity, i);
		}
	}

	/**
	 * update an entity, usually called when committing an <code>update</code>
	 * operation
	 * 
	 * @param entity
	 *            entity to update
	 * @throws Exception
	 */
	static void updateEntity(Entity entity) throws Exception {
		logger.debug("Update Entity " + entity);
		Entity origin = beforeCommit.get(entity);
		for (Field f : SQLParser.getFields(getRealEntityClass(entity))) {
			f.setAccessible(true);
			f.set(origin, f.get(entity));
		}
	}

	private static Logger logger = Logger.getLogger(EntityManager.class);

	/**
	 * a set containing registered entities.
	 */
	private static Set<Class<?>> entityClassSet = new HashSet<Class<?>>();

	/**
	 * maps record identifier to Entity object.
	 * 
	 * @see RecordIdentifier
	 */
	private static Map<RecordIdentifier, Entity> entities = new HashMap<RecordIdentifier, Entity>();

	/**
	 * maps current in use entity , to original entities, in case of transaction
	 * rolling back.
	 */
	static Map<Entity, Entity> beforeCommit = new HashMap<Entity, Entity>();

	private EntityManager() {
	}
}
