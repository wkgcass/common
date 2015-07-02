package cass.pure.loader;

public interface ClassLoaderManager {
	Class<?> load(String className) throws ClassNotFoundException;
	Class<?> update(String className) throws ClassNotFoundException;
}
