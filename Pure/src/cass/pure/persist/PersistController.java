package cass.pure.persist;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import cass.pure.persist.exception.CannotConnectException;
import cass.pure.persist.pool.Properties;
import cass.pure.persist.support.ResultSetStorage;

/**
 * Provides low level persistence operations. <br/>
 * All direct database query should be performed through this class.
 * 
 * @author wkgcass
 *
 */
public class PersistController {

	public static void closeConnection(Connection conn) {
		try {
			pool.close(conn);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * invoke when finishing committing a transaction
	 * 
	 * @param recordIndex
	 *            a list containing sql record indexes
	 */
	static void commitTransaction(List<Long> recordIndex) {
		for (Transaction t : map.values()) {
			t.updateCommitedSQL(recordIndex);
		}
	}

	/**
	 * do query. the result set would be transformed into java
	 * List&lt;Map&lt;String,String&gt;&gt; with the same order
	 * 
	 * @param conn
	 *            connection to perform the query
	 * @param sql
	 *            an SQL Data Manipulation Language (DML) statement, such as
	 *            INSERT, UPDATE or DELETE; or an SQL statement that returns
	 *            nothing, such as a DDL statement.
	 * 
	 * @return result set in the form of List&lt;Map&lt;String,String&gt;&gt;
	 * @throws SQLException
	 */
	public static List<Map<String, String>> executeQuery(Connection conn,
			String sql) throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		List<Map<String, String>> retList = ResultSetStorage.storeResultSet(rs);
		rs.close();
		stmt.close();
		return retList;
	}

	/**
	 * do update.
	 * 
	 * @param conn
	 *            connection to perform the query
	 * @param sql
	 *            an SQL Data Manipulation Language (DML) statement, such as
	 *            INSERT, UPDATE or DELETE; or an SQL statement that returns
	 *            nothing, such as a DDL statement.
	 * 
	 * @return either (1) the row count for SQL Data Manipulation Language (DML)
	 *         statements or (2) 0 for SQL statements that return nothing
	 * 
	 * @throws SQLException
	 */
	public static int executeUpdate(Connection conn, String sql)
			throws SQLException {
		Statement stmt = conn.createStatement();
		int ret = stmt.executeUpdate(sql);
		stmt.close();
		return ret;
	}

	/**
	 * get connection info from annotations of <code>entityClass</code><br/>
	 * then invoke {@link #getConnection(String, String, String)}
	 * 
	 * @param entityClass
	 *            class of an entity
	 * @return retrieved connection
	 */
	public static Connection getConnection(Class<?> entityClass) {
		Properties properties = EntityManager.getConnProperties(entityClass);
		try {
			return getConnection(properties.url, properties.user,
					properties.pwd);
		} catch (Throwable t) {
			throw new CannotConnectException(t);
		}
	}

	/**
	 * retrieve a connection from connection pool.
	 * 
	 * @param url
	 *            Connection url
	 * @param user
	 *            Connection user name
	 * @param pwd
	 *            Connection password
	 * @return retrieved connection
	 * @throws Throwable
	 */
	public static Connection getConnection(String url, String user, String pwd)
			throws Throwable {
		if (null == pool)
			throw new NullPointerException("Pool Not Initialized");
		Connection conn = pool.getConnection(url, user, pwd);
		conn.setAutoCommit(false);
		return conn;
	}

	/**
	 * get or generate a transaction object provided for the current thread.
	 * 
	 * @return transaction provided for the current thread
	 */
	public static Transaction getTransaction() {
		Thread current = Thread.currentThread();
		if (map.containsKey(current)) {
			return map.get(current);
		} else {
			Transaction t = new Transaction();
			map.put(current, t);
			return t;
		}
	}

	/**
	 * release a transaction
	 * 
	 * @param t
	 *            transaction object
	 */
	static void releaseTransaction(Transaction t) {
		map.values().remove(t);
	}

	/**
	 * Set pool that this framework uses.
	 * 
	 * @param pool
	 *            Connection pool implementation
	 * @throws Throwable
	 */
	public static void setPool(Pool pool) throws Throwable {
		if (pool != null) {
			logger.info("Connection pool : using "
					+ pool.getClass().getSimpleName());
			PersistController.pool = pool;
		}
	}

	private static Logger logger = Logger.getLogger(PersistController.class);

	/**
	 * connection pool
	 */
	private static Pool pool;

	/**
	 * maps thread to transaction. <br/>
	 * all threads would be given different transaction objects.
	 */
	private static Map<Thread, Transaction> map = new ConcurrentHashMap<Thread, Transaction>();

	private PersistController() {
	}
}
