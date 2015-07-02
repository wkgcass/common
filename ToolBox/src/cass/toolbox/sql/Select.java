package cass.toolbox.sql;

public class Select extends SQL{
	Select(StringBuffer sb, String... columns){
		this.sb=sb;
		sb.append("SELECT ");
		sb.append(columns[0]);
		for(int i=1;i<columns.length; ++i){
			sb.append(", ");
			sb.append(columns[i]);
		}
	}
	public Select into(String table){
		sb.append(" INTO ");
		sb.append(table);
		return this;
	}
}