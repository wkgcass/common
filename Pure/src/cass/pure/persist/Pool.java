package cass.pure.persist;

import java.sql.Connection;

/**
 * an interface adapts different pools
 * 
 * @author wkgcass
 *
 */
public interface Pool {
	/**
	 * the pool can be used. usually the existence of class of the pool would be
	 * checked.
	 * 
	 * @return true if can use, false otherwise.
	 */
	boolean canUse();

	/**
	 * retrieve a connection.
	 * 
	 * @param url
	 *            connection url.
	 * @param user
	 *            user to connect the database.
	 * @param pwd
	 *            password to connect the database.
	 * @return retrieved connection.
	 * @throws Throwable
	 */
	Connection getConnection(String url, String user, String pwd)
			throws Throwable;

	/**
	 * close a connection through the pool.<br/>
	 * 
	 * @param conn
	 *            the connection to be closed.
	 * @throws Throwable
	 */
	void close(Connection conn) throws Throwable;
}
