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

	private static R1ArgInterface<?, ?> copy = e -> e;

	@SuppressWarnings("unchecked")
	public static <T> R1ArgInterface<T, T> copy() {
		return (R1ArgInterface<T, T>) copy;
	}

	private static function<?> copyFunc = $(copy);

	@SuppressWarnings("unchecked")
	public static <T> function<T> copyFunc() {
		return (function<T>) copyFunc;
	}

	private static R1ArgInterface<String, String> trim = s -> s.trim();

	public static R1ArgInterface<String, String> trim() {
		return trim;
	}

	private static function<String> trimFunc = $(trim);

	public static function<String> trimFunc() {
		return trimFunc;
	}

	private static R2ArgsInterface<?, ?, ?> values = (k, v) -> v;

	@SuppressWarnings("unchecked")
	public static <K, V> R2ArgsInterface<V, K, V> values() {
		return (R2ArgsInterface<V, K, V>) values;
	}

	private static function<?> valuesFunc = $(values);

	@SuppressWarnings("unchecked")
	public static <V> function<V> valuesFunc() {
		return (function<V>) valuesFunc;
	}

	private static R2ArgsInterface<?, ?, ?> keys = (k, v) -> k;

	@SuppressWarnings("unchecked")
	public static <K, V> R2ArgsInterface<K, K, V> keys() {
		return (R2ArgsInterface<K, K, V>) keys;
	}

	private static function<?> keysFunc = $(keys);

	@SuppressWarnings("unchecked")
	public static <K> function<K> keyFunc() {
		return (function<K>) keysFunc;
	}

	private static R2ArgsInterface<Entry<?, ?>, ?, ?> entries = (k, v) -> new Entry<>(k, v);

	@SuppressWarnings("unchecked")
	public static <K, V, En extends Entry<K, V>> R2ArgsInterface<En, K, V> entries() {
		return (R2ArgsInterface<En, K, V>) entries;
	}

	private static function<Entry<?, ?>> entriesFunc = $(entries);

	@SuppressWarnings("unchecked")
	public static <K, V, En extends Entry<K, V>> function<En> entriesFunc() {
		return (function<En>) entriesFunc;
	}

	private static R2ArgsInterface<Entry<?, ?>, ?, ?> mapCopy = entries;

	@SuppressWarnings("unchecked")
	public static <K, V, En extends Entry<K, V>> R2ArgsInterface<En, K, V> mapCopy() {
		return (R2ArgsInterface<En, K, V>) mapCopy;
	}

	private static function<Entry<?, ?>> mapCopyFunc = entriesFunc;

	@SuppressWarnings("unchecked")
	public static <K, V, En extends Entry<K, V>> function<En> mapCopyFunc() {
		return (function<En>) mapCopyFunc;
	}

	static final Break Control_Break = new Break();
	static final Remove Control_Remove = new Remove();
	static final Continue Control_Continue = new Continue();
}