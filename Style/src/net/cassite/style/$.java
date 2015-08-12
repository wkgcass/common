package net.cassite.style;

import java.util.function.Predicate;

import net.cassite.style.Supportters.function;
import net.cassite.style.control.Break;
import net.cassite.style.control.Continue;
import net.cassite.style.control.Remove;
import net.cassite.style.interfaces.R1ArgInterface;
import net.cassite.style.interfaces.R2ArgsInterface;

public class $ extends style {
	private $() {
	}

	private static Predicate<?> alwaysTrue = t -> true;

	@SuppressWarnings("unchecked")
	public static <T> Predicate<T> alwaysTrue() {
		return (Predicate<T>) alwaysTrue;
	}

	// TODO
	public static R1ArgInterface<?, ?> copy = e -> e;

	public static function<?> copyFunc = $(copy);

	public static R1ArgInterface<String, String> trim = s -> s.trim();

	public static function<String> trimFunc = $(trim);

	public static R2ArgsInterface<?, ?, ?> values = (k, v) -> v;

	public static function<?> valuesFunc = $(values);

	public static R2ArgsInterface<?, ?, ?> keys = (k, v) -> k;

	public static function<?> keysFunc = $(keys);

	public static R2ArgsInterface<Entry<?, ?>, ?, ?> entries = (k, v) -> new Entry<>(k, v);

	public static function<Entry<?, ?>> entriesFunc = $(entries);

	public static R2ArgsInterface<Entry<?, ?>, ?, ?> mapCopy = entries;

	public static function<Entry<?, ?>> mapCopyFunc = entriesFunc;

	static final Break Control_Break = new Break();
	static final Remove Control_Remove = new Remove();
	static final Continue Control_Continue = new Continue();
}
