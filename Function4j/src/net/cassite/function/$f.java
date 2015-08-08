package net.cassite.function;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import net.cassite.function.interfaces.Void2ArgInterface;
import net.cassite.function.interfaces.R0ArgInterface;
import net.cassite.function.interfaces.R1ArgInterface;
import net.cassite.function.interfaces.R2ArgsInterface;
import net.cassite.function.interfaces.R3ArgsInterface;
import net.cassite.function.interfaces.R4ArgsInterface;
import net.cassite.function.interfaces.R5ArgsInterface;
import net.cassite.function.interfaces.R6ArgsInterface;
import net.cassite.function.interfaces.R7ArgsInterface;
import net.cassite.function.interfaces.Void0ArgInterface;
import net.cassite.function.interfaces.Void1ArgInterface;
import net.cassite.function.interfaces.RNArgsInterface;
import net.cassite.function.loopcontrol.Break;
import net.cassite.function.loopcontrol.Continue;
import net.cassite.function.loopcontrol.Remove;

public class $f {
	public static final Break Break = new Break();
	public static final Remove Remove = new Remove();
	public static final Continue Continue = new Continue();

	public static class ArrayFuncSup<T> {
		private final T[] array;

		ArrayFuncSup(T[] array) {
			this.array = array;
		}

		public void forEach(Void1ArgInterface<T> func) {
			for (T t : array) {
				try {
					func.accept(t);
				} catch (Throwable throwable) {
					if (throwable instanceof Break) {
						break;
					} else if (throwable instanceof Continue) {
						continue;
					} else {
						throw new RuntimeException(throwable);
					}
				}
			}
		}

		public T first() {
			return array[0];
		}

		public <R, Coll extends Collection<R>> Tramsformer<R, T, Coll> to(Coll collection) {
			return new Tramsformer<>(array, collection);
		}

		public static class Tramsformer<R, T, Coll extends Collection<R>> {
			private final Coll collection;
			private final T[] array;

			Tramsformer(T[] array, Coll collection) {
				this.array = array;
				this.collection = collection;
			}

			public Coll via(R1ArgInterface<R, T> method) {
				for (T t : array) {
					R ret;
					try {
						ret = method.apply(t);
					} catch (Throwable e) {
						if (e instanceof Break) {
							break;
						} else if (e instanceof Continue) {
							continue;
						} else {
							throw new RuntimeException(e);
						}
					}
					collection.add(ret);
				}
				return collection;
			}
		}
	}

	public static class IterableFuncSup<T> {
		public final Iterable<T> iterable;

		IterableFuncSup(Iterable<T> iterable) {
			this.iterable = iterable;
		}

		public void forEach(Void1ArgInterface<T> func) {
			Iterator<T> it = iterable.iterator();
			while (it.hasNext()) {
				try {
					func.accept(it.next());
				} catch (Throwable throwable) {
					if (throwable instanceof Break) {
						break;
					} else if (throwable instanceof Remove) {
						it.remove();
					} else if (throwable instanceof Continue) {
						continue;
					} else {
						throw new RuntimeException(throwable);
					}
				}
			}
		}

		public T first() {
			return iterable.iterator().next();
		}

		public <R, Coll extends Collection<R>> Tramsformer<R, T, Coll> to(Coll collection) {
			return new Tramsformer<>(iterable, collection);
		}

		public static class Tramsformer<R, T, Coll extends Collection<R>> {
			private final Coll collection;
			private final Iterable<T> iterable;

			Tramsformer(Iterable<T> iterable, Coll collection) {
				this.iterable = iterable;
				this.collection = collection;
			}

			public Coll via(R1ArgInterface<R, T> method) {
				for (T t : iterable) {
					R ret;
					try {
						ret = method.apply(t);
					} catch (Throwable e) {
						if (e instanceof Break) {
							break;
						} else if (e instanceof Continue) {
							continue;
						} else {
							throw new RuntimeException(e);
						}
					}
					collection.add(ret);
				}
				return collection;
			}
		}
	}

	public static class CollectionFuncSup<T> extends IterableFuncSup<T> {
		CollectionFuncSup(Collection<T> collection) {
			super(collection);
		}

		public CollectionFuncSup<T> add(T t) {
			if (!(iterable instanceof Collection)) {
				throw new UnsupportedOperationException();
			}
			Collection<T> coll = (Collection<T>) iterable;
			coll.add(t);
			return this;
		}
	}

	public static class MapFuncSup<K, V> {
		private Map<K, V> map;

		MapFuncSup(Map<K, V> map) {
			this.map = map;
		}

		public void forEach(Void2ArgInterface<K, V> func) {
			Iterator<K> it = map.keySet().iterator();
			while (it.hasNext()) {
				K k = it.next();
				try {
					func.accept(k, map.get(k));
				} catch (Throwable t) {
					if (t instanceof Break) {
						break;
					} else if (t instanceof Remove) {
						it.remove();
					} else if (t instanceof Continue) {
						continue;
					} else {
						throw new RuntimeException(t);
					}
				}
			}
		}

		public MapFuncSup<K, V> append(K key, V value) {
			map.put(key, value);
			return this;
		}

		public static class TransformerColl<K, V, R, Coll extends Collection<R>> {
			private Map<K, V> map;
			private Coll collection;

			TransformerColl(Map<K, V> map, Coll collection) {
				this.map = map;
				this.collection = collection;
			}

			public Coll via(R2ArgsInterface<R, K, V> method) {
				for (K key : map.keySet()) {
					try {
						R val = method.apply(key, map.get(key));
						collection.add(val);
					} catch (Throwable e) {
						if (e instanceof Continue) {
							continue;
						} else if (e instanceof Break) {
							break;
						} else {
							throw new RuntimeException(e);
						}
					}
				}
				return collection;
			}
		}

		public <R, Coll extends Collection<R>> TransformerColl<K, V, R, Coll> to(Coll collection) {
			return new TransformerColl<K, V, R, Coll>(map, collection);
		}

		public static class TransformerMap<K, V, K2, V2, M extends Map<K2, V2>> {
			private Map<K, V> map;
			private M toMap;

			TransformerMap(Map<K, V> map, M toMap) {
				this.map = map;
				this.toMap = toMap;
			}

			public M via(R2ArgsInterface<Entry<K2, V2>, K, V> method) {
				for (K key : map.keySet()) {
					try {
						Entry<K2, V2> val = method.apply(key, map.get(key));
						toMap.put(val.key, val.value);
					} catch (Throwable e) {
						if (e instanceof Continue) {
							continue;
						} else if (e instanceof Break) {
							break;
						} else {
							throw new RuntimeException(e);
						}
					}
				}
				return toMap;
			}
		}

		public <K2, V2, M extends Map<K2, V2>> TransformerMap<K, V, K2, V2, M> to(M m) {
			return new TransformerMap<K, V, K2, V2, M>(map, m);
		}
	}

	public static <T> ArrayFuncSup<T> $(T[] array) {
		return new ArrayFuncSup<T>(array);
	}

	public static <T> IterableFuncSup<T> $(Iterable<T> it) {
		return new IterableFuncSup<>(it);
	}

	public static <T> CollectionFuncSup<T> $(Collection<T> coll) {
		return new CollectionFuncSup<>(coll);
	}

	public static <K, V> MapFuncSup<K, V> $(Map<K, V> map) {
		return new MapFuncSup<>(map);
	}

	public static <T> void For(T i, Predicate<T> condition, UnaryOperator<T> increment, Void1ArgInterface<T> loop) {
		for (T ii = i; condition.test(ii); ii = increment.apply(ii)) {
			try {
				loop.accept(ii);
			} catch (Throwable t) {
				if (t instanceof Break) {
					break;
				} else {
					throw new RuntimeException(t);
				}
			}
		}
	}

	public static void While(BooleanSupplier condition, Void0ArgInterface loop) {
		while (condition.getAsBoolean()) {
			try {
				loop.invoke();
			} catch (Throwable t) {
				if (t instanceof Break) {
					break;
				} else {
					throw new RuntimeException(t);
				}
			}
		}
	}

	public static class function<R> {

		private RNArgsInterface<R> body;
		private R0ArgInterface<R> body0;
		private R1ArgInterface<R, Object> body1;
		private R2ArgsInterface<R, Object, Object> body2;
		private R3ArgsInterface<R, Object, Object, Object> body3;
		private R4ArgsInterface<R, Object, Object, Object, Object> body4;
		private R5ArgsInterface<R, Object, Object, Object, Object, Object> body5;
		private R6ArgsInterface<R, Object, Object, Object, Object, Object, Object> body6;
		private R7ArgsInterface<R, Object, Object, Object, Object, Object, Object, Object> body7;

		function(RNArgsInterface<R> body) {
			this.body = body;
		}

		function(R0ArgInterface<R> body) {
			this.body0 = body;
		}

		@SuppressWarnings("unchecked")
		function(R1ArgInterface<R, ?> body) {
			this.body1 = (R1ArgInterface<R, Object>) body;
		}

		@SuppressWarnings("unchecked")
		function(R2ArgsInterface<R, ?, ?> body) {
			this.body2 = (R2ArgsInterface<R, Object, Object>) body;
		}

		@SuppressWarnings("unchecked")
		function(R3ArgsInterface<R, ?, ?, ?> body) {
			this.body3 = (R3ArgsInterface<R, Object, Object, Object>) body;
		}

		@SuppressWarnings("unchecked")
		function(R4ArgsInterface<R, ?, ?, ?, ?> body) {
			this.body4 = (R4ArgsInterface<R, Object, Object, Object, Object>) body;
		}

		@SuppressWarnings("unchecked")
		function(R5ArgsInterface<R, ?, ?, ?, ?, ?> body) {
			this.body5 = (R5ArgsInterface<R, Object, Object, Object, Object, Object>) body;
		}

		@SuppressWarnings("unchecked")
		function(R6ArgsInterface<R, ?, ?, ?, ?, ?, ?> body) {
			this.body6 = (R6ArgsInterface<R, Object, Object, Object, Object, Object, Object>) body;
		}

		@SuppressWarnings("unchecked")
		function(R7ArgsInterface<R, ?, ?, ?, ?, ?, ?, ?> body) {
			this.body7 = (R7ArgsInterface<R, Object, Object, Object, Object, Object, Object, Object>) body;
		}

		public R apply(Object... args) {
			try {
				if (args.length == 0) {
					return body0.apply();
				} else if (args.length == 1) {
					return body1.apply(args[0]);
				} else if (args.length == 2) {
					return body2.apply(args[0], args[1]);
				} else if (args.length == 3) {
					return body3.apply(args[0], args[1], args[2]);
				} else if (args.length == 4) {
					return body4.apply(args[0], args[1], args[2], args[3]);
				} else if (args.length == 5) {
					return body5.apply(args[0], args[1], args[2], args[3], args[4]);
				} else if (args.length == 6) {
					return body6.apply(args[0], args[1], args[2], args[3], args[4], args[5]);
				} else if (args.length == 7) {
					return body7.apply(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
				} else {
					return body.invoke(args);
				}
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}

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
}
