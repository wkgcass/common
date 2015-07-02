package cass.toolbox.sql;

public class SQL {
	public static final boolean DESC = true;
	public static final boolean ASC = false;

	public static class insert extends SQL {
		insert(StringBuffer sb) {
			this.sb = sb;
			sb.append("INSERT");
		}

		public static insert into(String table) {
			StringBuffer sb = new StringBuffer();
			insert i = new insert(sb);
			sb.append(" INTO ");
			sb.append(table);
			return i;
		}

		public insert _(String... columns) {
			sb.append(" (");
			sb.append(columns[0]);
			for (int i = 1; i < columns.length; ++i) {
				sb.append(", ");
				sb.append(columns[i]);
			}
			sb.append(")");
			return this;
		}

		public insert values(Object... values) {
			sb.append(" VALUES (");
			sb.append(values[0]);
			for (int i = 1; i < values.length; ++i) {
				sb.append(", ");
				sb.append(format(values[i]));
			}
			sb.append(")");
			return this;
		}
	}

	public static class delete {

		private StringBuffer sb;

		public delete(StringBuffer stringBuffer) {
			this.sb = stringBuffer;
			sb.append("DELETE");
		}

		public static From from(String table) {
			return new From((new delete(new StringBuffer()).sb), table);
		}

	}

	protected StringBuffer sb;

	protected SQL() {
	}

	public static Select select(String... columns) {
		return new Select(new StringBuffer(), columns);
	}

	public static Update update(String table) {
		return new Update(new StringBuffer(), table);
	}

	public From from(String table) {
		return new From(this.sb, table);
	}

	public Where where(String column) {
		return new Where(this.sb, column);
	}

	public OrderBy orderBy(String s) {
		return new OrderBy(this.sb, s);
	}

	public OrderBy orderBy(String s, boolean desc) {
		return new OrderBy(this.sb, s, desc);
	}

	public String toString() {
		return this.sb.toString() + ";";
	}

	protected static String format(Object o) {
		String ret = o.toString();
		ret.replace("'", "''");
		ret = '\'' + ret + '\'';
		return ret;
	}
}
