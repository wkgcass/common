package cass.toolbox.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Utility_ {
	private Utility_() {
	}

	public static Object stringToObject(Object o) {
		if (null == o)
			return null;
		String eValue = null;
		try {
			eValue = (String) o;
		} catch (ClassCastException e) {
			return o;
		}
		eValue = eValue.trim();
		if (eValue.equalsIgnoreCase("null")
				|| eValue.equalsIgnoreCase("nullptr") || eValue.equals("")) {
			return null;
		} else if (eValue.equalsIgnoreCase("true")) {
			return true;
		} else if (eValue.equalsIgnoreCase("false")) {
			return false;
		} else if (isNumeric(eValue)) {
			if (eValue.contains(".")) {
				return Double.parseDouble(eValue);
			} else {
				return Integer.parseInt(eValue);
			}
		} else {
			return eValue;
		}
	}

	public static boolean isNumeric(String str) {
		int dotCount = 0;
		for (int i = str.length() - 1; i >= 0; --i) {
			if (str.charAt(i) < '0' || str.charAt(i) > '9') {
				if (str.charAt(i) == '.') {
					if (1 == dotCount)
						return false;
					else
						dotCount = 1;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	public static String toJSON(Object target) throws IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchMethodException {
		return toJSON(target, false);
	}

	public static String toJSON(Object target, boolean parsePrivate)
			throws IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchMethodException {
		return toJSON(target, parsePrivate, Object.class);
	}

	public static String toJSON(Object target, boolean parsePrivate,
			Class<?> top) throws IllegalArgumentException,
			IllegalAccessException, SecurityException, NoSuchMethodException {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		Class<?> targetClass = target.getClass();
		while (targetClass != top && targetClass != Object.class) {
			for (Field f : targetClass.getDeclaredFields()) {
				int mod = f.getModifiers();
				if (Modifier.isStatic(mod))
					continue;
				if (!Modifier.isPublic(mod)) {
					if (parsePrivate) {
						if (!sb.toString().equals("{"))
							sb.append(", ");
						f.setAccessible(true);
						sb.append("\"").append(f.getName()).append("\":\"")
								.append(f.get(target)).append("\"");
					} else
						continue;
				} else {
					if (!sb.toString().equals("{"))
						sb.append(", ");
					sb.append("\"").append(f.getName()).append("\":\"")
							.append(f.get(target)).append("\"");
				}
			}
			targetClass = targetClass.getSuperclass();
		}
		sb.append("}");
		return sb.toString();
	}
	/*
	StringBuffer sb = new StringBuffer();
	if (target == null) {
		sb.append("null");
	} else if (target.getClass().isArray()) {
		// array
		sb.append("[");
		for (int i = 0; i < Array.getLength(target); ++i) {
			if (!sb.toString().equals("[")) {
				sb.append(", ");
			}
			sb.append(toJSON(Array.get(target, i)));
		}
		sb.append("]");
	} else if (Collection.class.isInstance(target)) {
		// list || set
		Collection<?> coll = (Collection<?>) target;
		sb.append("[");
		for (Object o : coll) {
			if (!sb.toString().equals("[")) {
				sb.append(", ");
			}
			sb.append(toJSON(o, parsePrivate, top));
		}
		sb.append("]");
	} else if (Number.class.isInstance(target)) {
		sb.append(target);
	} else if (Boolean.class.isInstance(target)) {
		sb.append(target);
	} else if (String.class.isInstance(target)) {
		sb.append("\"");
		for (int i = 0; i < ((String) target).length(); ++i) {
			sb.append("\\u").append(
					Integer.toHexString(((String) target).charAt(i)));
		}
		sb.append("\"");
	} else {
		sb.append("{");
		Class<?> targetClass = target.getClass();
		while (targetClass != top && targetClass != Object.class) {
			for (Field f : targetClass.getDeclaredFields()) {
				int mod = f.getModifiers();
				if (Modifier.isStatic(mod))
					continue;
				if (!Modifier.isPublic(mod)) {
					if (parsePrivate) {
						if (!sb.toString().equals("{"))
							sb.append(", ");
						f.setAccessible(true);
						Object o = f.get(target);
						sb.append("\"").append(f.getName()).append("\":")
								.append(toJSON(o, parsePrivate, top));
					} else
						continue;
				} else {
					if (!sb.toString().equals("{"))
						sb.append(", ");
					Object o = f.get(target);
					sb.append("\"").append(f.getName()).append("\":")
							.append(toJSON(o, parsePrivate, top));
				}
			}
			targetClass = targetClass.getSuperclass();
		}
		sb.append("}");
	}
	return sb.toString();
	*/
}
