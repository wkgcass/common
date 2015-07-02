package cass.pure.persist;

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

import cass.pure.persist.annotation.Column;
import cass.pure.persist.annotation.DB;
import cass.pure.persist.annotation.Maps;
import cass.pure.persist.annotation.Reference;
import cass.pure.persist.annotation.Table;
import cass.pure.persist.exception.EntityParseException;
import cass.pure.persist.exception.PKNotFoundException;

/**
 * a bridge between java objects to sql databases.<br/>
 * it provides methods to parse an object to a sql clause.<br/>
 * or parse a sql result into a java object.
 * 
 * @author wkgcass
 *
 */
public abstract class SQLParser {

	private static Logger logger = Logger.getLogger(SQLParser.class);

	/**
	 * registered parsers
	 */
	private static Set<SQLParser> parsers = new HashSet<SQLParser>();

	protected SQLParser() {
	}

	/**
	 * determines wheather this parser can handle given database.
	 * 
	 * @param db
	 *            url of a db connection
	 * @return true if the parser can handle the connection, false otherwise.
	 */
	public abstract boolean canHandle(String db);

	/**
	 * retrieve info from given field and build sql to create the column.
	 * 
	 * @param f
	 * @return an incomplete sql clause which creates a column.
	 */
	public abstract String buildColumnCreationSql(Field f);

	/**
	 * retrieve info from given class and build sql to create a table.
	 * 
	 * @param entityClass
	 * @return a sql which creates a table.
	 */
	public abstract String buildTableCreationSql(Class<?> entityClass);

	/**
	 * get data type of database corresponds to the field.
	 * 
	 * @param f
	 * @return data type of the database
	 */
	public abstract String getSqlType(Field f);

	/**
	 * convert a java object into a sql string
	 * 
	 * @param o
	 * @return
	 * @throws Throwable
	 */
	public abstract String toSQLValue(Object o) throws Throwable;

	/**
	 * convert a string retrieved from query result to a java object
	 * 
	 * @param type
	 *            type of the java object to be generated
	 * @param value
	 *            the string
	 * @return
	 * @throws Throwable
	 */
	public abstract Object toObject(Class<?> type, String value)
			throws Throwable;

	/**
	 * get the string with escape characters
	 * 
	 * @param str
	 *            original string
	 * @return the string with escape characters
	 */
	public abstract String addEscapeChar(String str);

	/**
	 * retrieve last generated value, usually called when setting an auto
	 * increment field with generated value.
	 * 
	 * @param conn
	 *            connection
	 * @return generated value
	 * @throws Exception
	 */
	public abstract int getLastGeneratedValue(Connection conn) throws Exception;

	/**
	 * generate update sql
	 * 
	 * @param entity
	 * @return
	 */
	public static String updateSQL(Entity entity) {
		SQLParser parser = getParser(entity.getClass());
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE ");
		sb.append(getTableName(entity.getClass()));
		Field[] fields = getFields(entity.getClass());
		try {
			int i = 0;
			for (; i < fields.length; ++i) {
				if (isPK(fields[i])) {
					continue;
				}
				if (Collection.class.isAssignableFrom(fields[i].getType())
						|| Map.class.isAssignableFrom(fields[i].getType()))
					continue;
				Field f = fields[i];
				f.setAccessible(true);
				Object fvalue = f.get(entity);
				if (fvalue == null)
					continue;
				sb.append(" SET ");
				sb.append(getColumnName(fields[i]));
				sb.append(" = '");
				if (Entity.class.isAssignableFrom(fields[i].getType())) {
					Class<?> target = f.getType();
					SQLParser p = SQLParser.getParser(target);
					Field pk = SQLParser.getPK(target);
					pk.setAccessible(true);
					sb.append(p.addEscapeChar(p.toSQLValue(pk.get(f.get(entity)))));
				} else {
					sb.append(parser.addEscapeChar(parser.toSQLValue(f
							.get(entity))));
				}
				sb.append("'");
				break;
			}
			++i;
			for (; i < fields.length; ++i) {
				if (isPK(fields[i])) {
					continue;
				}
				if (Collection.class.isAssignableFrom(fields[i].getType())
						|| Map.class.isAssignableFrom(fields[i].getType()))
					continue;
				Field f = fields[i];
				f.setAccessible(true);
				Object fvalue = f.get(entity);
				if (fvalue == null)
					continue;
				sb.append(", ");
				sb.append(getColumnName(fields[i]));
				sb.append(" = '");
				if (Entity.class.isAssignableFrom(fields[i].getType())) {
					Class<?> target = f.getType();
					SQLParser p = SQLParser.getParser(target);
					Field pk = SQLParser.getPK(target);
					pk.setAccessible(true);
					sb.append(p.addEscapeChar(p.toSQLValue(pk.get(f.get(entity)))));
				} else {
					sb.append(parser.addEscapeChar(parser.toSQLValue(f
							.get(entity))));
				}
				sb.append("'");
			}
			sb.append(" WHERE ")
					.append(getColumnName(getPK(entity.getClass())))
					.append(" = '");
			Field f = getPK(entity.getClass());
			f.setAccessible(true);

			sb.append(parser.addEscapeChar(parser.toSQLValue(f.get(entity))))
					.append("';");

			logger.debug("Generated SQL String : " + sb.toString());
			return sb.toString();
		} catch (Throwable e) {
			throw new EntityParseException(entity, e);
		}
	}

	/**
	 * generate insert sql
	 * 
	 * @param entity
	 * @return
	 * @throws Throwable
	 */
	public static String insertSQL(Entity entity) throws Throwable {
		SQLParser parser = getParser(entity.getClass());
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO ");
		sb.append(getTableName(entity.getClass()));
		Field[] fields = getFields(entity.getClass());
		Map<String, String> map = new HashMap<String, String>();
		try {
			for (Field f : fields) {
				if (Collection.class.isAssignableFrom(f.getType())
						|| Map.class.isAssignableFrom(f.getType())) {
					continue;
				}
				if (isAutoIncrement(f))
					continue;
				f.setAccessible(true);
				Object value = f.get(entity);
				if (value == null
						|| (f.getType().isPrimitive() && (value.equals(0) || value
								.equals(0.0))))
					continue;
				if (Entity.class.isAssignableFrom(f.getType())) {
					Class<?> targetClass = f.getType();
					SQLParser p = getParser(targetClass);
					Field targetField = getPK(targetClass);
					targetField.setAccessible(true);
					Object pk = targetField.get(value);
					map.put(getColumnName(f), p.addEscapeChar(p.toSQLValue(pk)));
				} else {
					map.put(getColumnName(f),
							parser.addEscapeChar(parser.toSQLValue(value)));
				}
			}
		} catch (Throwable e) {
			throw new EntityParseException(entity, e);
		}
		String[] columns = new String[map.size()];
		if (columns.length == 0)
			throw new EntityParseException(entity, "No enough not-null fields");
		String[] values = new String[columns.length];
		int i = -1;
		for (String s : map.keySet()) {
			++i;
			columns[i] = s;
			values[i] = map.get(s);
		}

		sb.append(" (");
		boolean flag = false;
		for (String column : columns) {
			if (flag) {
				sb.append(", ");
			}
			sb.append(column);
			flag = true;
		}
		sb.append(") VALUES (");
		flag = false;
		for (String value : values) {
			if (flag) {
				sb.append(", ");
			}

			sb.append("'").append(parser.toSQLValue(value)).append("'");
			flag = true;
		}
		sb.append(");");

		logger.debug("Generated SQL String : " + sb.toString());
		return sb.toString();
	}

	/**
	 * generate delete sql<br/>
	 * calls {@link #deleteSQL(Class, Object)}
	 * 
	 * @param entity
	 * @return
	 * @throws Throwable
	 */
	public static String deleteSQL(Entity entity) throws Throwable {
		Field pkField = getPK(entity.getClass());
		pkField.setAccessible(true);
		return deleteSQL(entity.getClass(), pkField.get(entity));
	}

	/**
	 * generate delete sql with given class and primary key.<br/>
	 * calls {@link #deleteSQL(String, String, String, SQLParser)}
	 * 
	 * @param entityClass
	 * @param pkValue
	 * @return
	 * @throws Throwable
	 */
	private static String deleteSQL(Class<?> entityClass, Object pkValue)
			throws Throwable {
		SQLParser parser = getParser(entityClass);
		return deleteSQL(getTableName(entityClass),
				getColumnName(getPK(entityClass)), parser.toSQLValue(pkValue),
				parser);
	}

	/**
	 * generate delete sql with given arguments
	 * 
	 * @param tableName
	 * @param pkColName
	 * @param pkValue
	 * @param parser
	 * @return
	 */
	private static String deleteSQL(String tableName, String pkColName,
			String pkValue, SQLParser parser) {
		String sql = "DELETE FROM " + tableName + " WHERE " + pkColName
				+ " = '" + parser.addEscapeChar(pkValue) + "';";
		logger.debug("Generated SQL String : " + sql);
		return sql;
	}

	/**
	 * get generic class from Set<?> or List<?>
	 * 
	 * @param f
	 * @return
	 */
	static Class<?> getGenericClass(Field f) {
		Reference generic = f.getAnnotation(Reference.class);
		return generic.ref();
	}

	/**
	 * generate select sql with primary key<br/>
	 * calls {@link #selectByPKSQL(Class, String)}
	 * 
	 * @param entityClass
	 *            entity class
	 * @param pkValue
	 *            primary key value in the form of java object
	 * @return
	 * @throws Throwable
	 */
	public static String selectByPKSQL(Class<?> entityClass, Object pkValue)
			throws Throwable {
		SQLParser parser = getParser(entityClass);
		return selectByPKSQL(entityClass, parser.toSQLValue(pkValue));
	}

	public static Field getTargetForeignKey(Field f) throws Exception {
		if (Collection.class.isAssignableFrom(f.getType())) {
			Reference reference = f.getAnnotation(Reference.class);
			Class<?> targetClass = reference.ref();
			if (reference.foreign().equals("")) {
				for (Field f2 : getFields(targetClass)) {
					if (f2.getType().isAssignableFrom(f.getDeclaringClass())) {
						return f2;
					}
				}
				return null;
			} else {
				return targetClass.getDeclaredField(reference.foreign());
			}
		} else if (Map.class.isAssignableFrom(f.getType())) {
			Maps maps = f.getAnnotation(Maps.class);
			Class<?> targetClass = maps.ref();
			if (maps.foreign().equals("")) {
				for (Field f2 : getFields(targetClass)) {
					if (f2.getType().isAssignableFrom(f.getDeclaringClass())) {
						return f2;
					}
				}
				return null;
			} else {
				return targetClass.getDeclaredField(maps.foreign());
			}
		} else {
			return null;
		}
	}

	/**
	 * generate select sql without where clause, the result column label would
	 * be in this form: TABLE_COLUMN, all in lower case.<br/>
	 * sql format details can see the method : {@link #getTable_Column(Field)}
	 * 
	 * @param entityClass
	 * @return
	 * @throws Exception
	 */
	static StringBuffer selectPart(Class<?> entityClass) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ");
		Field[] fields = getFields(entityClass);
		Map<String, Map<String, Set<String>>> joinTable = new HashMap<String, Map<String, Set<String>>>();
		String pkColumnName = getColumnName(getPK(entityClass));
		int count = -1;
		for (Field f : fields) {
			++count;
			if (!sb.toString().equals("SELECT ")) {
				sb.append(", ");
			}
			if (Collection.class.isAssignableFrom(f.getType())) {
				Class<?> genericClass = getGenericClass(f);
				String tableName = getTableName(genericClass) + "_" + count;
				String pkCol = getColumnName(getPK(genericClass));
				sb.append(tableName).append(".").append(pkCol).append(" AS ")
						.append(getTable_Column(f));

				Map<String, Set<String>> targetMap;
				if (joinTable.containsKey(pkColumnName)) {
					targetMap = joinTable.get(pkColumnName);
				} else {
					targetMap = new HashMap<String, Set<String>>();
					joinTable.put(pkColumnName, targetMap);
				}
				Set<String> targetSet;
				if (targetMap.containsKey(tableName)) {
					targetSet = targetMap.get(tableName);
				} else {
					targetSet = new HashSet<String>();
					targetMap.put(tableName, targetSet);
				}
				targetSet.add(getColumnName(getTargetForeignKey(f)));

			} else if (Map.class.isAssignableFrom(f.getType())) {
				Maps maps = f.getAnnotation(Maps.class);
				String tableName = getTableName(maps.ref()) + "_" + count;
				String pkCol = getColumnName(getPK(maps.ref()));
				sb.append(tableName).append("_").append(pkCol).append(" AS ")
						.append(getTable_Column(f));

				Map<String, Set<String>> targetMap;
				if (joinTable.containsKey(pkColumnName)) {
					targetMap = joinTable.get(pkColumnName);
				} else {
					targetMap = new HashMap<String, Set<String>>();
					joinTable.put(pkColumnName, targetMap);
				}
				Set<String> targetSet;
				if (targetMap.containsKey(tableName)) {
					targetSet = targetMap.get(tableName);
				} else {
					targetSet = new HashSet<String>();
					targetMap.put(tableName, targetSet);
				}
				targetSet.add(getColumnName(getTargetForeignKey(f)));

			} else if (Entity.class.isAssignableFrom(f.getType())) {
				Class<?> targetClass = f.getType();
				String tableName = getTableName(targetClass) + "_" + count;
				String pkCol = getColumnName(getPK(targetClass));
				sb.append(tableName).append(".").append(pkCol).append(" AS ")
						.append(getTable_Column(f));

				Map<String, Set<String>> targetMap;
				if (joinTable.containsKey(getColumnName(f))) {
					targetMap = joinTable.get(getColumnName(f));
				} else {
					targetMap = new HashMap<String, Set<String>>();
					joinTable.put(getColumnName(f), targetMap);
				}
				Set<String> targetSet;
				if (targetMap.containsKey(tableName)) {
					targetSet = targetMap.get(tableName);
				} else {
					targetSet = new HashSet<String>();
					targetMap.put(tableName, targetSet);
				}
				targetSet.add(pkCol);

			} else {
				sb.append(getTableName(entityClass)).append(".")
						.append(getColumnName(f)).append(" AS ")
						.append(getTable_Column(f));
			}
		}

		String thisTableName = getTableName(entityClass);
		sb.append(" FROM ");
		sb.append(thisTableName);

		if (!joinTable.isEmpty()) {
			for (String col : joinTable.keySet()) {
				Map<String, Set<String>> map = joinTable.get(col);
				for (String tbl : map.keySet()) {
					sb.append(" LEFT JOIN ");
					sb.append(tbl.substring(0, tbl.lastIndexOf('_')))
							.append(" AS ").append(tbl);
					boolean and = false;
					for (String targetCol : map.get(tbl)) {
						if (and) {
							sb.append(" AND ");
						}
						and = true;
						sb.append(" ON ").append(thisTableName).append(".")
								.append(col).append("=").append(tbl)
								.append(".").append(targetCol);
					}
				}
			}
		}

		return sb;
	}

	/**
	 * generate select sql
	 * 
	 * @param entityClass
	 *            entity class
	 * @param pkValue
	 *            primary key value in the form of string
	 * @return
	 * @throws Exception
	 */
	private static String selectByPKSQL(Class<?> entityClass, String pkValue)
			throws Exception {
		SQLParser parser = getParser(entityClass);
		StringBuffer sb = selectPart(entityClass);

		sb.append(" WHERE ");
		sb.append(getTableName(entityClass)).append(".");
		sb.append(getColumnName(getPK(entityClass)));
		sb.append("='");
		sb.append(parser.addEscapeChar(pkValue));
		sb.append("';");

		logger.debug("Generated SQL String : " + sb.toString());
		return sb.toString();
	}

	/**
	 * add where clause to string buffer
	 * 
	 * @param sample
	 *            the sample to generate where clause
	 * @param sb
	 *            string buffer to append on
	 * @throws Throwable
	 */
	static void whereClause(Entity sample, StringBuffer sb) throws Throwable {
		SQLParser parser = getParser(sample.getClass());
		sb.append(" WHERE ");
		int count = -1;
		for (Field f : getFields(EntityManager.getRealEntityClass(sample))) {
			++count;
			Class<?> type = f.getType();
			if (Map.class.isAssignableFrom(type)
					|| Collection.class.isAssignableFrom(type))
				continue;
			f.setAccessible(true);
			Object value = f.get(sample);
			if (value == null
					|| (f.getType().isPrimitive() && (value.equals(0) || value
							.equals(0.0)))) {
				continue;
			} else {
				if (!sb.toString().endsWith(" WHERE ")) {
					sb.append(" AND ");
				}
				if (Entity.class.isAssignableFrom(type)) {
					SQLParser p = getParser(type);
					Field targetPK = getPK(type);
					targetPK.setAccessible(true);
					String colname = getColumnName(targetPK);
					sb.append(getTableName(type) + "_" + count + ".")
							.append(colname).append(" = '")
							.append(p.toSQLValue(targetPK.get(value)))
							.append("'");
				} else {
					sb.append(getTableName(sample.getClass())).append(".")
							.append(getColumnName(f)).append("='")
							.append(parser.toSQLValue(value)).append("'");
				}
			}
		}
	}

	/**
	 * generate select sql with given entity sample
	 * 
	 * @param sample
	 *            where clause would be generated with field values from sample
	 * @return
	 * @throws Throwable
	 */
	public static String selectSQL(Entity sample) throws Throwable {
		StringBuffer sb = selectPart(sample.getClass());
		whereClause(sample, sb);
		sb.append(";");
		logger.debug("Generated SQL String : " + sb.toString());
		return sb.toString();
	}

	/**
	 * get declared fields from non proxy class of entityClass
	 * 
	 * @param entityClass
	 * @return
	 */
	protected static Field[] getFields(Class<?> entityClass) {
		entityClass = EntityManager.getRealEntityClass(entityClass);
		List<Field> tmpList = new ArrayList<Field>();
		while (entityClass != Entity.class) {
			for (Field f : entityClass.getDeclaredFields()) {
				tmpList.add(f);
			}
			entityClass = entityClass.getSuperclass();
		}
		Field[] ret = new Field[tmpList.size()];
		for (int i = 0; i < tmpList.size(); ++i) {
			ret[i] = tmpList.get(i);
		}
		return ret;
	}

	/**
	 * determines whether the field is an auto increment field.
	 * 
	 * @param f
	 * @return
	 */
	public static boolean isAutoIncrement(Field f) {
		Column column = f.getAnnotation(Column.class);
		if (null == column) {
			if (isPK(f))
				return true;
			else
				return false;
		} else {
			return column.autoIncrement();
		}
	}

	/**
	 * get a list of primary values in the form of strings when doing cascading
	 * query.
	 * 
	 * @param f
	 * @param resultList
	 * @return
	 */
	static List<String> getCascadingPKValueStringList(Field f,
			List<Map<String, String>> resultList) {
		List<String> ret = new ArrayList<String>();
		for (Map<String, String> result : resultList) {
			String pkValueString = result.get(getTable_Column(f));
			if (pkValueString != null)
				ret.add(pkValueString);
		}
		return ret;
	}

	/**
	 * get result list by primary key
	 * 
	 * @param entityClass
	 *            class of the entity
	 * @param pkValueString
	 *            primary key
	 * @return query result
	 * @throws Exception
	 */
	static List<Map<String, String>> getResultListByPK(Class<?> entityClass,
			String pkValueString) throws Exception {
		String sqlString = selectByPKSQL(entityClass, pkValueString);
		Connection conn = PersistController.getConnection(entityClass);
		List<Map<String, String>> resultList = PersistController.executeQuery(
				conn, sqlString);
		PersistController.closeConnection(conn);
		return resultList;
	}

	/**
	 * retrieve primary key field from entityClass
	 * 
	 * @param entityClass
	 * @return
	 */
	public static Field getPK(Class<?> entityClass) {
		entityClass = EntityManager.getRealEntityClass(entityClass);
		Field[] fields = getFields(entityClass);
		for (Field f : fields) {
			if (f.isAnnotationPresent(Column.class)) {
				Column column = f.getAnnotation(Column.class);
				if (column.primary())
					return f;
			}
		}
		for (Field f : fields) {
			if (f.getName().endsWith("id")) {
				return f;
			}
		}
		throw new PKNotFoundException(entityClass);
	}

	/**
	 * determines whether the field is primary key
	 * 
	 * @param f
	 * @return
	 */
	public static boolean isPK(Field f) {
		Class<?> entityClass = f.getDeclaringClass();
		return getPK(entityClass).equals(f);
	}

	/**
	 * get column name of the field
	 * 
	 * @param field
	 * @return
	 */
	public static String getColumnName(Field field) {
		Column column = field.getAnnotation(Column.class);
		if (null == column) {
			return field.getName().toLowerCase();
		} else {
			if (column.name().equals("")) {
				return field.getName().toLowerCase();
			} else {
				return column.name().toLowerCase();
			}
		}
	}

	/**
	 * get table name of the entity class
	 * 
	 * @param entityClass
	 * @return
	 */
	public static String getTableName(Class<?> entityClass) {
		Table table = entityClass.getAnnotation(Table.class);
		if (null == table)
			return entityClass.getSimpleName().toLowerCase();
		else {
			if (table.name().equals("")) {
				return entityClass.getSimpleName().toLowerCase();
			} else {
				return table.name().toLowerCase();
			}
		}
	}

	/**
	 * register a parser
	 * 
	 * @param parserClass
	 */
	public static void registerParser(Class<?> parserClass) {
		if (SQLParser.class.isAssignableFrom(parserClass)) {
			for (SQLParser p : parsers) {
				if (p.getClass() == parserClass)
					return;
			}
			try {
				parsers.add((SQLParser) parserClass.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * retrieve parser that can handle given entityClass' db
	 * 
	 * @param entityClass
	 * @return the parser found. or null if no parsers found
	 */
	public static SQLParser getParser(Class<?> entityClass) {
		entityClass = EntityManager.getRealEntityClass(entityClass);
		String url = null;
		if (entityClass.isAnnotationPresent(DB.class)) {
			DB db = entityClass.getAnnotation(DB.class);
			url = db.url();
		} else {
			Package pkg = entityClass.getPackage();
			if (pkg.isAnnotationPresent(DB.class)) {
				DB db = pkg.getAnnotation(DB.class);
				url = db.url();
			} else {
				return null;
			}
		}
		for (SQLParser parser : parsers) {
			if (parser.canHandle(url)) {
				return parser;
			}
		}
		return null;
	}

	/**
	 * get column label as TABLE_COLUMN. <br/>
	 * <ul>
	 * <li>if f.getType() is subclass of Entity, target class'
	 * tableName_targetPkColumnName would be returned.</li>
	 * <li>if f.getType() is Collection, generic type's tableName_pkcolumnName
	 * would be returned.</li>
	 * <li>if f.getType() is Map, annotation Maps would be retrieved and
	 * refTableName_refPkColumnName would be returned.</li>
	 * <li>in other cases, tableNameOfDeclaringClass_ColumnNameOfThisField would
	 * be returned.</li>
	 * </ul>
	 * 
	 * @param f
	 * @return
	 */
	static String getTable_Column(Field f) {
		Class<?> targetTable;
		Field targetField;
		if (Entity.class.isAssignableFrom(f.getType())) {
			targetTable = f.getType();
			targetField = getPK(targetTable);
		} else if (Collection.class.isAssignableFrom(f.getType())) {
			targetTable = getGenericClass(f);
			targetField = getPK(targetTable);
		} else if (Map.class.isAssignableFrom(f.getType())) {
			Maps maps = f.getAnnotation(Maps.class);
			targetTable = maps.ref();
			targetField = getPK(targetTable);
		} else {
			targetTable = f.getDeclaringClass();
			targetField = f;
		}
		return getTableName(targetTable) + "_" + getColumnName(targetField)
				+ "_" + getTableName(f.getDeclaringClass()) + "_"
				+ getColumnName(f);
	}
}
