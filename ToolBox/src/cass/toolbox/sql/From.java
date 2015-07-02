package cass.toolbox.sql;

public class From extends SQL {

	public From(StringBuffer sb, String table) {
		this.sb=sb;
		sb.append(" FROM ");
		sb.append(table);
	}

}
