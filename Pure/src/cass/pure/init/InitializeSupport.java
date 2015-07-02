package cass.pure.init;

public interface InitializeSupport {
	void doInit(String[] args);
	void addClass(Class<?> cls);
	double priority();
}
