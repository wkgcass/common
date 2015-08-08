package net.cassite.function.interfaces;

@FunctionalInterface
public interface RNArgsInterface<R> {
	R invoke(Object... args) throws Throwable;
}
