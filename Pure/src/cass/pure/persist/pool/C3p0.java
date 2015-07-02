package cass.pure.persist.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.DataSources;

import cass.pure.persist.Pool;

public class C3p0 implements Pool {
	private Map<Properties, DataSource> pooledDataSources = new HashMap<Properties, DataSource>();

	@Override
	public boolean canUse() {
		try {
			this.getClass().getClassLoader()
					.loadClass("com.mchange.v2.c3p0.DataSources");
			return true;
		} catch (Throwable e) {
			return false;
		}
	}

	@Override
	public Connection getConnection(String url, String user, String pwd)
			throws SQLException {
		Properties p = new Properties();
		p.url = url;
		p.user = user;
		p.pwd = pwd;
		DataSource pooled = null;
		synchronized (pooledDataSources) {
			if (!pooledDataSources.containsKey(p)) {
				pooled = DataSources.pooledDataSource(DataSources
						.unpooledDataSource(url, user, pwd));
				pooledDataSources.put(p, pooled);
			} else {
				pooled = pooledDataSources.get(p);
			}
		}
		return pooled.getConnection();
	}

	@Override
	public void close(Connection conn) throws SQLException {
		if (null != conn) {
			if (!conn.isClosed()) {
				conn.close();
			}
		}
	}

}
