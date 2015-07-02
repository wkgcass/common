package cass.toolbox.sql;

import java.sql.Connection;
import java.sql.SQLException;

public class Transaction {
	public String SQL;
	private Connection conn;
	public Transaction(Connection conn) throws SQLException{
		conn.setAutoCommit(false);
		SQL="";
	}
	public void commit() throws SQLException{
		conn.createStatement().execute(SQL);
		conn.commit();
		SQL="";
	}
	public void rollback() throws SQLException{
		conn.rollback();
	}
	
	public void clearTransactionList(){
		SQL="";
	}
}
