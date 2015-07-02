package cass.pure.persist;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cass.pure.persist.exception.TargetClosedException;
import cass.pure.persist.pool.Properties;

public final class Transaction {

	private static Logger logger = Logger.getLogger(Transaction.class);

	/**
	 * transaction has no sql to commit
	 */
	private boolean commited = true;
	/**
	 * transaction is closed
	 */
	private boolean isClosed = false;

	/**
	 * [static]current sql index
	 */
	private static Long currentIndex = -1L;
	/**
	 * [static]maps indexes to sql records
	 */
	private static Map<Long, SQLRecord> sqlMap = new LinkedHashMap<Long, SQLRecord>();

	/**
	 * entity managed by this transaction
	 */
	private Set<Entity> entities = new HashSet<Entity>();
	/**
	 * connections used when committing a transaction
	 */
	private List<Connection> connectionList = new ArrayList<Connection>();
	/**
	 * a list containing indexes
	 */
	private List<Long> sqlList = new ArrayList<Long>();
	/**
	 * a list of entities to be removed
	 */
	private Set<Entity> toRemove = new HashSet<Entity>();
	/**
	 * a list of entities to be saved
	 */
	private Set<Entity> toInsert = new HashSet<Entity>();

	Transaction() {
	}

	/**
	 * add a sql index into <code>sqlList</code>
	 * 
	 * @param index
	 *            sql record index
	 */
	void executeUpdate(Long index) {
		sqlList.add(index);
	}

	/**
	 * generate a sql index and then add a sql into <code>sqlList</code> and
	 * <code>sqlMap</code> <br/>
	 * connection properties will be retrieved from <code>entityClass</code>
	 * 
	 * @param entityClass
	 *            class of the entity
	 * @param sql
	 *            sql to be performed
	 * @return
	 */
	Long executeUpdate(Class<?> entityClass, String sql) {
		if (isClosed) {
			throw new TargetClosedException(this);
		}
		synchronized (currentIndex) {
			currentIndex += 1;
			SQLRecord record = new SQLRecord();
			record.properties = EntityManager.getConnProperties(entityClass);
			record.sql = sql;
			sqlMap.put(currentIndex, record);
			sqlList.add(currentIndex);
			return currentIndex;
		}
	}

	/**
	 * save an entity
	 * 
	 * @param entity
	 *            entity to be saved
	 * @return generated sql record index
	 * @throws Throwable
	 */
	Long executeSave(Entity entity) throws Throwable {
		if (isClosed) {
			throw new TargetClosedException(this);
		}
		synchronized (currentIndex) {
			currentIndex += 1;
			SQLRecord record = new SQLRecord();
			record.entity = entity;
			record.properties = EntityManager.getConnProperties(entity
					.getClass());
			record.remove = false;
			record.save = true;
			sqlMap.put(currentIndex, record);
			sqlList.add(currentIndex);
			return currentIndex;
		}
	}

	/**
	 * remove an entity
	 * 
	 * @param entity
	 *            entity to be removed
	 * @return generated sql record index
	 * @throws Throwable
	 */
	Long executeRemove(Entity entity) throws Throwable {
		if (isClosed) {
			throw new TargetClosedException(this);
		}
		synchronized (currentIndex) {
			currentIndex += 1;
			SQLRecord record = new SQLRecord();
			record.entity = entity;
			record.properties = EntityManager.getConnProperties(entity
					.getClass());
			record.remove = true;
			record.save = false;
			sqlMap.put(currentIndex, record);
			sqlList.add(currentIndex);
			return currentIndex;
		}
	}

	/**
	 * clear the transaction without committing
	 */
	public void clear() {
		sqlList.clear();
		entities.clear();
		for (Connection conn : connectionList) {
			PersistController.closeConnection(conn);
		}
		connectionList.clear();
		toInsert.clear();
		toRemove.clear();
	}

	/**
	 * commit the transaction
	 * 
	 * @throws Throwable
	 */
	public void commit() throws Throwable {
		logger.debug("Transaction committing...");
		if (isClosed) {
			throw new TargetClosedException(this);
		}
		// join all with same connection properties
		Map<Properties, List<SQLRecord>> tmp = new LinkedHashMap<Properties, List<SQLRecord>>();
		for (SQLRecord r : sqlMap.values()) {
			List<SQLRecord> list;
			if (tmp.containsKey(r.properties)) {
				list = tmp.get(r.properties);
			} else {
				list = new ArrayList<SQLRecord>();
				tmp.put(r.properties, list);
			}
			list.add(r);
			if (r.remove) {
				toRemove.add(r.entity);
			}
			if (r.save) {
				toInsert.add(r.entity);
			}
		}
		for (Entity entity : entities) {
			if (entity.isModified()) {
				String sql = SQLParser.updateSQL(entity);
				SQLRecord record = new SQLRecord();
				record.entity = entity;
				Properties p = EntityManager.getConnProperties(entity
						.getClass());
				record.properties = p;
				record.remove = false;
				record.save = false;
				record.sql = sql;
				List<SQLRecord> records;
				if (tmp.containsKey(p)) {
					records = tmp.get(p);
				} else {
					records = new ArrayList<SQLRecord>();
					tmp.put(p, records);
				}
				records.add(record);
			}
		}

		logger.debug(tmp.size() + " changes will be taken. \ndetails:" + tmp);

		// do committing
		for (Properties p : tmp.keySet()) {
			Connection conn = PersistController.getConnection(p.url, p.user,
					p.pwd);
			connectionList.add(conn);
			for (SQLRecord sql : tmp.get(p)) {
				if (sql.sql == null) {
					if (sql.remove) {
						PersistController.executeUpdate(conn,
								SQLParser.deleteSQL(sql.entity));
					} else if (sql.save) {
						PersistController.executeUpdate(conn,
								SQLParser.insertSQL(sql.entity));
					} else {
						PersistController.executeUpdate(conn, sql.sql);
					}
				} else {
					PersistController.executeUpdate(conn, sql.sql);
				}
				if (sql.save) {
					EntityManager.setAIPK(conn, sql.entity);
				}
			}
		}

		// commit connections and close them
		for (Connection conn : connectionList) {
			conn.commit();
			PersistController.closeConnection(conn);
		}
		connectionList.clear();

		// commit entity changes
		for (Entity entity : entities) {
			EntityManager.updateEntity(entity);
		}
		EntityManager.removeEntity(toRemove);
		for (Entity e : entities) {
			e.persistLevel = Entity.PERSIST;
		}
		for (Entity e : toRemove) {
			e.persistLevel = Entity.FREE;
		}

		// commit transactions
		PersistController.commitTransaction(sqlList);
		clear();
	}

	/**
	 * roll back a transaction
	 * 
	 * @throws Exception
	 */
	public void rollback() throws Exception {
		logger.debug("Transaction rolling back...");
		if (isClosed) {
			throw new TargetClosedException(this);
		}
		// roll back entities
		for (Entity entity : entities) {
			EntityManager.resetEntity(entity);
		}

		// roll back connections
		for (Connection conn : connectionList) {
			conn.rollback();
			PersistController.closeConnection(conn);
		}
		connectionList.clear();
	}

	public void close() throws Exception {
		if (isClosed) {
			throw new TargetClosedException(this);
		}
		if (!commited) {
			try {
				commit();
			} catch (Throwable e) {
				rollback();
			}
			commited = true;
		}
		for (Entity e : entities) {
			e.releaseTransaction(this);
		}
		PersistController.releaseTransaction(this);
		isClosed = true;
	}

	void updateCommitedSQL(List<Long> indexes) {
		sqlMap.keySet().removeAll(indexes);
	}

	void registerEntity(Entity entity) {
		entities.add(entity);
	}
}

class SQLRecord {
	public Properties properties;
	public String sql;
	public Entity entity;
	public boolean remove;
	public boolean save;

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (null == o)
			return false;
		if (o instanceof SQLRecord) {
			SQLRecord r = (SQLRecord) o;
			if (r.properties.equals(this.properties) && r.sql.equals(this.sql)
					&& r.remove == remove) {
				return true;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		return properties.hashCode() * 100 + sql.hashCode() * 10
				+ (remove ? 1 : 0);
	}

	public String toString() {
		if (remove) {
			return "{entity=" + entity + ", action=remove}";
		} else if (save) {
			return "{entity=" + entity + ", action=save}";
		} else if (sql == null) {
			return "{entity=" + entity + ", action=update}";
		} else {
			return "{sql=" + sql + "}";
		}
	}
}
