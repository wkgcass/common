package cass.pure.persist.parser;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.jmx.snmp.Timestamp;

import cass.pure.persist.Entity;
import cass.pure.persist.EntityManager;
import cass.pure.persist.PersistController;
import cass.pure.persist.SQLParser;
import cass.pure.persist.annotation.Column;
import cass.pure.persist.annotation.Table;
import cass.pure.persist.exception.CannotCreateTableException;
import cass.pure.persist.exception.ColumnTypeNotFoundException;
import cass.pure.persist.exception.QueryResultParseException;

public class MySQLParser extends SQLParser {

	@Override
	public boolean canHandle(String db) {
		if (db.substring(5, 10).equalsIgnoreCase("mysql"))
			return true;
		return false;
	}

	@Override
	public String buildColumnCreationSql(Field f) {
		StringBuffer sb = new StringBuffer();
		sb.append(getColumnName(f).toLowerCase());
		sb.append(" ");
		String sqlType = getSqlType(f);
		if (null == sqlType)
			return null;
		sb.append(sqlType);
		boolean isPK = isPK(f);
		if (isPK) {
			sb.append(" PRIMARY KEY");
		}
		Column column = f.getAnnotation(Column.class);
		if (null != column) {
			if (column.autoIncrement()) {
				sb.append(" AUTO_INCREMENT");
			}
			if (column.notNull()) {
				sb.append(" NOT NULL");
			}
			if (column.unique()) {
				sb.append(" UNIQUE");
			}
			if (!column.Default().equals(
					"cass.purify.persist.no_default_string_value")) {
				sb.append(" DEFAULT '");
				sb.append(addEscapeChar(column.Default()));
				sb.append("'");
			}
		} else {
			if (isPK
					&& (f.getType().equals(int.class) || f.getType().equals(
							Integer.class))) {
				sb.append(" AUTO_INCREMENT");
			}
		}
		return sb.toString();
	}

	@Override
	public String buildTableCreationSql(Class<?> entityClass) {
		entityClass = EntityManager.getRealEntityClass(entityClass);
		StringBuffer sb = new StringBuffer();
		sb.append("CREATE TABLE ");
		sb.append(getTableName(entityClass).toLowerCase());
		sb.append(" ( ");

		Field[] fields = getFields(entityClass);
		if (fields.length == 0)
			throw new CannotCreateTableException(entityClass);
		{
			try {
				int i = 0;
				String columnSql = buildColumnCreationSql(fields[i]);
				while (columnSql == null) {
					++i;
					if (i == fields.length)
						break;
					columnSql = buildColumnCreationSql(fields[i]);
				}
				if (i != fields.length) {
					sb.append(columnSql);
					++i;
					for (; i < fields.length; ++i) {
						String colCreation = buildColumnCreationSql(fields[i]);
						if (null == colCreation)
							continue;
						sb.append(", ");
						sb.append(colCreation);
					}
				}
			} catch (Exception e) {
				throw new CannotCreateTableException(entityClass, e);
			}
		}
		sb.append(")");
		Table table = entityClass.getAnnotation(Table.class);
		if (null == table) {
			sb.append(" ENGINE=InnoDB DEFAULT CHARSET=UTF8;");
		} else {
			sb.append(" ENGINE=");
			sb.append(table.engine());
			sb.append(" DEFAULT CHARSET=");
			sb.append(table.charset());
			sb.append(";");
		}
		return sb.toString();
	}

	@Override
	public String getSqlType(Field f) {
		Class<?> type = f.getType();
		if (type.equals(int.class) || type.equals(Integer.class)) {
			return "INT";
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			return "DOUBLE";
		} else if (type.equals(String.class)) {
			return "VARCHAR(255)";
		} else if (type.equals(BigDecimal.class)) {
			return "DECIMAL";
		} else if (type.equals(java.sql.Date.class)) {
			return "DATE";
		} else if (type.equals(Time.class)) {
			return "TIME";
		} else if (type.equals(Timestamp.class)
				|| type.equals(java.util.Date.class)) {
			return "TIMESTAMP";
		} else if (type.isEnum()) {
			Object[] constants = type.getEnumConstants();
			if (constants.length == 0)
				return "ENUM()";
			StringBuffer sb = new StringBuffer();
			sb.append("ENUM( '");
			sb.append(constants[0].toString());
			sb.append("'");
			for (int i = 1; i < constants.length; ++i) {
				sb.append(", '");
				sb.append(constants[i].toString());
				sb.append("'");
			}
			sb.append(" )");
			return sb.toString();
		} else if (type.equals(char.class) || type.equals(Character.class)) {
			return "CHAR(2)";
		} else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return "ENUM( 'true', 'false' )";
		} else if (Entity.class.isAssignableFrom(type)) {
			SQLParser p = getParser(type);
			return p.getSqlType(getPK(type));
		} else if (Set.class.isAssignableFrom(type)) {
			return null;
		} else if (List.class.isAssignableFrom(type)) {
			return null;
		} else if (Map.class.isAssignableFrom(type)) {
			return null;
		} else {
			throw new ColumnTypeNotFoundException(f);
		}
	}

	@Override
	public String toSQLValue(Object o) throws IllegalArgumentException,
			IllegalAccessException {
		if (o instanceof java.util.Date) {
			return DateFormat.getDateTimeInstance().format((java.util.Date) o);
		} else if (o instanceof Entity) {
			Field field = getPK(o.getClass());
			field.setAccessible(true);
			return toSQLValue(field.get(o));
		} else {
			return o.toString();
		}
	}

	@Override
	public Object toObject(Class<?> type, String value) throws Throwable {
		if (value == null)
			return null;
		if (type == int.class || type == Integer.class) {
			return Integer.parseInt(value);
		} else if (type == double.class || type == Double.class) {
			return Double.parseDouble(value);
		} else if (type == String.class) {
			return value;
		} else if (type == BigDecimal.class) {
			return new BigDecimal(value);
		} else if (type == java.sql.Date.class) {
			DateFormat df = DateFormat.getDateInstance();
			java.util.Date date = df.parse(value);
			return new java.sql.Date(date.getTime());
		} else if (type == Time.class) {
			DateFormat df = DateFormat.getDateTimeInstance();
			java.util.Date date = df.parse(value);
			return new Time(date.getTime());
		} else if (type == Timestamp.class) {
			DateFormat df = DateFormat.getDateTimeInstance();
			java.util.Date date = df.parse(value);
			return new java.sql.Timestamp(date.getTime());
		} else if (type == java.util.Date.class) {
			DateFormat df = DateFormat.getDateTimeInstance();
			return df.parse(value);
		} else if (type.isEnum()) {
			for (Object e : type.getEnumConstants()) {
				if (e.toString().equalsIgnoreCase(value)) {
					return e;
				}
			}
			throw new QueryResultParseException("Enum constant not found.");
		} else if (type == char.class || type == Character.class) {
			return value.charAt(0);
		} else if (type == boolean.class || type == Boolean.class) {
			return Boolean.parseBoolean(value);
		} else if (Entity.class.isAssignableFrom(type)) {
			SQLParser p = getParser(type);
			return p.toObject(getPK(type).getType(), value);
		} else {
			throw new QueryResultParseException("Unsupported type.");
		}
	}

	@Override
	public String addEscapeChar(String str) {
		return str.replace("\\", "\\\\").replace("'", "\\'")
				.replace("\"", "\\\"");
	}

	@Override
	public int getLastGeneratedValue(Connection conn) throws SQLException {
		List<Map<String, String>> result = PersistController.executeQuery(conn,
				"SELECT @@IDENTITY");
		Iterator<String> it = result.get(0).values().iterator();
		return Integer.parseInt(it.next());
	}
}
