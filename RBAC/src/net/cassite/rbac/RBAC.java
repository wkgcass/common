package net.cassite.rbac;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import net.cassite.sql.SQL;

public class RBAC<User> {
        private final Map<Integer, RestrictionUR<User>> ur = new HashMap<Integer, RestrictionUR<User>>();
        private final Map<Integer, RestrictionRP> rp = new HashMap<Integer, RestrictionRP>();
        private final JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();

        protected final ComboPooledDataSource ds;

        protected RBAC(String dbType, String url, String user, String pwd) throws SQLException {
                // get connection pool
                this.ds = new ComboPooledDataSource(dbType);

                SQL sql = new SQL();
                // fill restrictions
                Connection conn = getConection();
                ResultSet set = conn.createStatement().executeQuery(sql.select("Id", "Class", "Code").from("RestrictionRP").toString());
                while (set.next()) {
                        int id = set.getInt("Id");
                        String className = set.getString("Class");
                        String code = set.getString("Code");
                        
                        compiler.
                }

                set = conn.createStatement().executeQuery(sql.select("Id", "Class", "Code").from("RestrictionUR").toString());
        }

        protected Connection getConection() throws SQLException {
                return ds.getConnection();
        }
}
