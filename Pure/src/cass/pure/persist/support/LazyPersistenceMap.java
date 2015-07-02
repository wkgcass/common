package cass.pure.persist.support;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cass.pure.persist.Entity;
import cass.pure.persist.exception.ParseException;

public class LazyPersistenceMap implements Map<String, Object> {

	private List<Entity> list;

	private Field targetField;
	private Field valueField;

	public LazyPersistenceMap(Field targetField, Field valueField,
			List<Entity> backedList) throws SQLException {
		this.list = backedList;
		this.targetField = targetField;
		this.valueField = valueField;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	private String getTarget(Entity t) {
		targetField.setAccessible(true);
		try {
			return (String) targetField.get(t);
		} catch (Exception e) {
			throw new ParseException(e);
		}
	}

	private Object getValue(Entity t) {
		valueField.setAccessible(true);
		try {
			return valueField.get(t);
		} catch (Exception e) {
			throw new ParseException(e);
		}
	}

	@Override
	public boolean containsKey(Object arg0) {
		for (Entity t : list) {
			String target = getTarget(t);
			if (target.equals(arg0))
				return true;
		}
		return false;
	}

	@Override
	public boolean containsValue(Object arg0) {
		for (Entity t : list) {
			Object value = getValue(t);
			if (value.equals(arg0))
				return true;
		}
		return false;
	}

	public class Entry implements Map.Entry<String, Object> {
		private String target;
		private Object value;

		public Entry(String target, Object value) {
			this.target = target;
			this.value = value;
		}

		@Override
		public String getKey() {
			return target;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public Object setValue(Object value) {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		Set<Map.Entry<String, Object>> ret = new HashSet<Map.Entry<String, Object>>();
		for (Entity t : list) {
			ret.add(new Entry(getTarget(t), getValue(t)));
		}
		return ret;
	}

	@Override
	public Object get(Object arg0) {
		for (Entity t : list) {
			Object target = getTarget(t);
			if (target.equals(arg0))
				return getValue(t);
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		Set<String> ret = new HashSet<String>();
		for (Entity t : list) {
			ret.add(getTarget(t));
		}
		return ret;
	}

	@Override
	public Object put(String arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public Collection<Object> values() {
		List<Object> ret = new ArrayList<Object>();
		for (Entity t : list) {
			ret.add(getValue(t));
		}
		return ret;
	}

}
