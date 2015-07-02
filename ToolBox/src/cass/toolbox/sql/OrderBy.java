package cass.toolbox.sql;

public class OrderBy extends SQL {

	OrderBy(StringBuffer sb, String s, boolean desc) {
		this.sb=sb;
		sb.append(" ORDER BY ");
		sb.append(s);
		sb.append(" ");
		if(desc){
			sb.append("DESC");
		}else{
			sb.append("ASC");
		}
	}
	OrderBy(StringBuffer sb, String s){
		this.sb=sb;
		sb.append(" ORDER BY ");
		sb.append(s);
	}
	public OrderBy and(String s, boolean desc){
		sb.append(", ");
		sb.append(s);
		sb.append(" ");
		if(desc){
			sb.append("DESC");
		}else{
			sb.append("ASC");
		}
		return this;
	}
	public OrderBy and(String s){
		sb.append(", ");
		sb.append(s);
		return this;
	}

}
