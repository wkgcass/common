package cass.toolbox.sql;

public class Update extends SQL {
	Update(StringBuffer sb, String table){
		this.sb=sb;
		sb.append("UPDATE ");
		sb.append(table);
	}
	public class Set extends SQL{
		Set(StringBuffer sb, String column, Object value){
			this.sb=sb;
			sb.append(" SET ");
			sb.append(column);
			sb.append("=");
			sb.append(format(value));
		}
		public Set and(String column, Object value){
			sb.append(", ");
			sb.append(column);
			sb.append("=");
			sb.append(format(value));
			return this;
		}
	}
	public Set set(String column, Object value){
		return new Set(sb,column,value);
	}
}
