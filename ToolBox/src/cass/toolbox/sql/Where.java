package cass.toolbox.sql;

public class Where extends SQL {
	Where(StringBuffer sb, String column) {
		this.sb = sb;
		sb.append(" WHERE ");
		sb.append(column);
	}

	public Where equal(Object o) {
		sb.append("=");
		sb.append(format(o));
		return this;
	}

	public Where gt(Comparable<?> c) {
		sb.append(">");
		sb.append(format(c));
		return this;
	}

	public Where lt(Comparable<?> c) {
		sb.append("<");
		sb.append(format(c));
		return this;
	}

	public Where gteql(Comparable<?> c) {
		sb.append(">=");
		sb.append(format(c));
		return this;
	}

	public Where lteql(Comparable<?> c) {
		sb.append("<=");
		sb.append(format(c));
		return this;
	}

	public Where nEql(Object o) {
		sb.append("<>");
		sb.append(format(o));
		return this;
	}

	public Where and(String column) {
		sb.append(" AND ");
		sb.append(column);
		return this;
	}

	public Where or(String column) {
		sb.append(" OR ");
		sb.append(column);
		return this;
	}

	public Where like(Object o) {
		sb.append(" LIKE ");
		sb.append(format(o));
		return this;
	}
}
