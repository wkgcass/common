package net.cassite.function.interfaces;

@FunctionalInterface
public interface R1ArgInterface<R, T> {
	R apply(T t) throws Throwable;
}
