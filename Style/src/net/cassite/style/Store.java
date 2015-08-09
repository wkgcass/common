package net.cassite.style;

public class Store<T> {
	T o;

	Store(T o) {
		this.o = o;
	}

	@Override
	public int hashCode() {
		return o.hashCode();
	}

	@Override
	public String toString() {
		return o.toString();
	}

	@Override
	public boolean equals(Object another) {
		return o.equals(another);
	}
}