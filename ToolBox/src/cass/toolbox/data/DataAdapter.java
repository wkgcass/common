package cass.toolbox.data;

public interface DataAdapter {
	/**
	 * get property value.
	 * @param key property name/key
	 * @return current property value.
	 */
	public Object getProperty(Object key);
	/**
	 * set property value.
	 * @param key property name
	 * @param value property value
	 * @return property value before editing.
	 */
	public Object setProperty(Object key,Object value);
}
