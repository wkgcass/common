package net.cassite.style;

import java.util.Collection;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import net.cassite.style.Supportters.ArrayFuncSup;
import net.cassite.style.Supportters.AsyncGroup;
import net.cassite.style.Supportters.CollectionFuncSup;
import net.cassite.style.Supportters.IterableFuncSup;
import net.cassite.style.Supportters.JSONLike;
import net.cassite.style.Supportters.MapFuncSup;
import net.cassite.style.Supportters.StyleRuntimeException;
import net.cassite.style.Supportters.SwitchBlock;
import net.cassite.style.Supportters.function;
import net.cassite.style.control.Break;
import net.cassite.style.control.Continue;
import net.cassite.style.control.Remove;
import net.cassite.style.interfaces.R0ArgInterface;
import net.cassite.style.interfaces.R1ArgInterface;
import net.cassite.style.interfaces.R2ArgsInterface;
import net.cassite.style.interfaces.R3ArgsInterface;
import net.cassite.style.interfaces.R4ArgsInterface;
import net.cassite.style.interfaces.R5ArgsInterface;
import net.cassite.style.interfaces.R6ArgsInterface;
import net.cassite.style.interfaces.R7ArgsInterface;
import net.cassite.style.interfaces.RNArgsInterface;
import net.cassite.style.interfaces.Void0ArgInterface;
import net.cassite.style.interfaces.Void1ArgInterface;
import net.cassite.style.interfaces.Void2ArgInterface;
import net.cassite.style.interfaces.Void3ArgInterface;
import net.cassite.style.interfaces.Void4ArgInterface;
import net.cassite.style.interfaces.Void5ArgInterface;
import net.cassite.style.interfaces.Void6ArgInterface;
import net.cassite.style.interfaces.Void7ArgInterface;
import net.cassite.style.interfaces.VoidNArgInterface;

public class style {

	// ┌─────────────────────────────────┐
	// │............function.............│
	// └─────────────────────────────────┘
	// void functions

	public static function<Object> $(VoidNArgInterface body) {
		return new function<Object>(body);
	}

	public static function<Object> $(Void0ArgInterface body) {
		return new function<Object>(body);
	}

	public static function<Object> $(Void1ArgInterface<?> body) {
		return new function<Object>(body);
	}

	public static function<Object> $(Void2ArgInterface<?, ?> body) {
		return new function<Object>(body);
	}

	public static function<Object> $(Void3ArgInterface<?, ?, ?> body) {
		return new function<Object>(body);
	}

	public static function<Object> $(Void4ArgInterface<?, ?, ?, ?> body) {
		return new function<Object>(body);
	}

	public static function<Object> $(Void5ArgInterface<?, ?, ?, ?, ?> body) {
		return new function<Object>(body);
	}

	public static function<Object> $(Void6ArgInterface<?, ?, ?, ?, ?, ?> body) {
		return new function<Object>(body);
	}

	public static function<Object> $(Void7ArgInterface<?, ?, ?, ?, ?, ?, ?> body) {
		return new function<Object>(body);
	}

	// functions with return value
	public static <R> function<R> $(RNArgsInterface<R> body) {
		return new function<R>(body);
	}

	public static <R> function<R> $(R0ArgInterface<R> body) {
		return new function<R>(body);
	}

	public static <R> function<R> $(R1ArgInterface<R, ?> body) {
		return new function<R>(body);
	}

	public static <R> function<R> $(R2ArgsInterface<R, ?, ?> body) {
		return new function<R>(body);
	}

	public static <R> function<R> $(R3ArgsInterface<R, ?, ?, ?> body) {
		return new function<R>(body);
	}

	public static <R> function<R> $(R4ArgsInterface<R, ?, ?, ?, ?> body) {
		return new function<R>(body);
	}

	public static <R> function<R> $(R5ArgsInterface<R, ?, ?, ?, ?, ?> body) {
		return new function<R>(body);
	}

	public static <R> function<R> $(R6ArgsInterface<R, ?, ?, ?, ?, ?, ?> body) {
		return new function<R>(body);
	}

	public static <R> function<R> $(R7ArgsInterface<R, ?, ?, ?, ?, ?, ?, ?> body) {
		return new function<R>(body);
	}

	// function support

	public static <T> Store<T> store(T o) {
		return new Store<T>(o);
	}

	public static <T> T $(Store<T> store) {
		return store.o;
	}

	// ┌─────────────────────────────────┐
	// │...........async&await...........│
	// └─────────────────────────────────┘

	public static AsyncGroup $(Async<?>... asyncs) {
		return new AsyncGroup(asyncs);
	}

	public static <R> R await(Async<R> async) {
		return async.await();
	}

	// ┌─────────────────────────────────┐
	// │........iteration control........│
	// └─────────────────────────────────┘

	public static void Break() throws Break {
		throw $.Control_Break;
	}

	public static void Remove() throws Remove {
		throw $.Control_Remove;
	}

	public static void Continue() throws Continue {
		throw $.Control_Continue;
	}

	// ┌─────────────────────────────────┐
	// │...collections, maps and arrays..│
	// └─────────────────────────────────┘

	public static <T> ArrayFuncSup<T> $(T[] array) {
		return new ArrayFuncSup<T>(array);
	}

	public static <T> IterableFuncSup<T> $(Iterable<T> it) {
		return new IterableFuncSup<>(it);
	}

	public static <T> CollectionFuncSup<T> $(Collection<T> coll) {
		return new CollectionFuncSup<>(coll);
	}

	@SafeVarargs
	public static <E, Coll extends Collection<E>> Coll $(Coll collection, E... elements) {
		for (E e : elements) {
			collection.add(e);
		}
		return collection;
	}

	public static <K, V> MapFuncSup<K, V> $(Map<K, V> map) {
		return new MapFuncSup<>(map);
	}

	public static <K, V, M extends Map<K, V>> M $(M map, JSONLike<K, V> entries) {
		for (K key : entries.keySet()) {
			map.put(key, entries.get(key));
		}
		return map;
	}

	// ┌─────────────────────────────────┐
	// │......basic grammar enhance......│
	// └─────────────────────────────────┘
	// for

	public static <T> void For(T i, Predicate<T> condition, UnaryOperator<T> increment, Void1ArgInterface<T> loop) {
		for (T ii = i; condition.test(ii); ii = increment.apply(ii)) {
			try {
				loop.accept(ii);
			} catch (Throwable t) {
				if (t instanceof Break) {
					break;
				} else {
					throw $(t);
				}
			}
		}
	}

	// while

	public static void While(BooleanSupplier condition, Void0ArgInterface loop) {
		while (condition.getAsBoolean()) {
			try {
				loop.invoke();
			} catch (Throwable t) {
				if (t instanceof Break) {
					break;
				} else {
					throw $(t);
				}
			}
		}
	}

	// switch

	public static <T> SwitchBlock<T> Switch(T t) {
		return new SwitchBlock<T>(t);
	}

	// ┌─────────────────────────────────┐
	// │............throwable............│
	// └─────────────────────────────────┘

	public static StyleRuntimeException $(Throwable t) {
		return new StyleRuntimeException(t);
	}

	// ┌─────────────────────────────────┐
	// │..............other..............│
	// └─────────────────────────────────┘
	// json like
	public static <K, V> JSONLike<K, V> $(K key, V value) {
		return new JSONLike<K, V>(key, value);
	}
}
