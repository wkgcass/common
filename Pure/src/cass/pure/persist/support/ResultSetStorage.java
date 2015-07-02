package cass.pure.persist.support;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultSetStorage {
	private ResultSetStorage() {
	}

	public static List<Map<String, String>> storeResultSet(ResultSet rs)
			throws SQLException {
		List<Map<String, String>> storage = new ArrayList<Map<String, String>>();
		ResultSetMetaData rsmd = rs.getMetaData();

		while (rs.next()) {
			Map<String, String> map = new LinkedHashMap<String, String>();
			for (int i = 1; i <= rsmd.getColumnCount(); ++i) {
				map.put(rsmd.getColumnLabel(i), rs.getString(i));
			}
			storage.add(map);
		}

		return storage;
	}
}
