package cass.toolbox.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Table {
	private String tblName;
	
	// column
	private Columns columns;
	public class Columns implements Iterable<Column>{
		@Override
		public Iterator<Column> iterator() {
			// TODO Auto-generated method stub
			return null;
		}
		public Column getColumn(String columnName){
			//TODO
			return null;
		}
		public Column getColumn(int index){
			//TODO
			return null;
		}
	}
	public class Column{
		private String columnName;
		private Class<?> sup;
		public boolean canBeNull;
		public String defaultValue;
	}
	
	// row
	private Rows rows;
	public class Rows implements Iterable<Row>{
		@Override
		public Iterator<Row> iterator() {
			// TODO Auto-generated method stub
			return null;
		}
		public <T> T getElem(Row row){
			//TODO
			return null;
		}
		public <T> T getElem(int row){
			//TODO
			return null;
		}
	}
	public class Row{
		Map<Column, Object> data=new HashMap<Column, Object>();
		public Row(Columns cols, String sql){
			for(Column c:cols){
				if(c.canBeNull){
					data.put(c, c.defaultValue);
				}else if(!c.canBeNull && null!=c.defaultValue){
					data.put(c, c.defaultValue);
				}else{
					
				}
			}
		}
		public void setData(Column column, Object elem){
			
		}
		public void setAll(Object... elements){
			
		}
		public <T> T getElem(Column col){
			//TODO
			return null;
		}
	}
	
	
}
