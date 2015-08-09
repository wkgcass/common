package net.cassite.style;

import net.cassite.style.interfaces.R1ArgInterface;
import net.cassite.style.interfaces.R2ArgsInterface;

public class Simplify {
	private Simplify() {
	}

	public static R1ArgInterface<?, ?> copy = (e) -> e;

	public static R2ArgsInterface<?, ?, ?> values = (k, v) -> v;

	public static R2ArgsInterface<?, ?, ?> keys = (k, v) -> k;

	public static R2ArgsInterface<Entry<?, ?>, ?, ?> entries = (k, v) -> new Entry<>(k, v);

	public static R2ArgsInterface<Entry<?, ?>, ?, ?> mapCopy = entries;
}
