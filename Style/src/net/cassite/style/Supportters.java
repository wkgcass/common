package net.cassite.style;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

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

public class Supportters extends style {
	public static class ArrayFuncSup<T> {
		private final T[] array;

		ArrayFuncSup(T[] array) {
			this.array = array;
		}

		public void forEach(Void1ArgInterface<T> func) {
			forEach($(func));
		}

		public void forEach(function<Object> func) {
			for (T t : array) {
				try {
					func.apply(t);
				} catch (Throwable throwable) {
					if (throwable instanceof StyleRuntimeException) {
						Throwable origin = ((StyleRuntimeException) throwable).origin();
						if (origin instanceof Break) {
							break;
						} else if (origin instanceof Continue) {
							continue;
						} else {
							throw ((StyleRuntimeException) throwable);
						}
					} else {
						throw $(throwable);
					}
				}
			}
		}

		public void forThose(Predicate<T> predicate, Void1ArgInterface<T> func) {
			forThose(predicate, $(func));
		}

		public void forThose(Predicate<T> predicate, function<Object> func) {
			for (T t : array) {
				try {
					if (predicate.test(t))
						func.apply(t);
				} catch (Throwable throwable) {
					if (throwable instanceof StyleRuntimeException) {
						Throwable origin = ((StyleRuntimeException) throwable).origin();
						if (origin instanceof Break) {
							break;
						} else if (origin instanceof Continue) {
							continue;
						} else {
							throw ((StyleRuntimeException) throwable);
						}
					} else {
						throw $(throwable);
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
				return via($(method));
			}

			public Coll via(function<R> method) {
				for (T t : array) {
					R ret;
					try {
						ret = method.apply(t);
					} catch (Throwable e) {
						if (e instanceof StyleRuntimeException) {
							Throwable origin = ((StyleRuntimeException) e).origin();
							if (origin instanceof Break) {
								break;
							} else if (origin instanceof Continue) {
								continue;
							} else {
								throw ((StyleRuntimeException) e);
							}
						} else {
							throw $(e);
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
			forEach($(func));
		}

		public void forEach(function<Object> func) {
			Iterator<T> it = iterable.iterator();
			while (it.hasNext()) {
				try {
					func.apply(it.next());
				} catch (Throwable throwable) {
					if (throwable instanceof StyleRuntimeException) {
						Throwable origin = ((StyleRuntimeException) throwable).origin();
						if (origin instanceof Break) {
							break;
						} else if (origin instanceof Continue) {
							continue;
						} else if (origin instanceof Remove) {
							it.remove();
						} else {
							throw ((StyleRuntimeException) throwable);
						}
					} else {
						throw $(throwable);
					}
				}
			}
		}

		public void forThose(Predicate<T> predicate, Void1ArgInterface<T> func) {
			forThose(predicate, $(func));
		}

		public void forThose(Predicate<T> predicate, function<Object> func) {
			Iterator<T> it = iterable.iterator();
			while (it.hasNext()) {
				try {
					T t = it.next();
					if (predicate.test(t))
						func.apply(t);
				} catch (Throwable throwable) {
					if (throwable instanceof StyleRuntimeException) {
						Throwable origin = ((StyleRuntimeException) throwable).origin();
						if (origin instanceof Break) {
							break;
						} else if (origin instanceof Continue) {
							continue;
						} else if (origin instanceof Remove) {
							it.remove();
						} else {
							throw ((StyleRuntimeException) throwable);
						}
					} else {
						throw $(throwable);
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
				return via($(method));
			}

			public Coll via(function<R> method) {
				for (T t : iterable) {
					R ret;
					try {
						ret = method.apply(t);
					} catch (Throwable e) {
						if (e instanceof StyleRuntimeException) {
							Throwable origin = ((StyleRuntimeException) e).origin();
							if (origin instanceof Break) {
								break;
							} else if (origin instanceof Continue) {
								continue;
							} else {
								throw ((StyleRuntimeException) e);
							}
						} else {
							throw $(e);
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
			forEach(style.$(func));
		}

		public void forEach(function<Object> func) {
			Iterator<K> it = map.keySet().iterator();
			while (it.hasNext()) {
				K k = it.next();
				try {
					func.apply(k, map.get(k));
				} catch (Throwable throwable) {
					if (throwable instanceof StyleRuntimeException) {
						Throwable origin = ((StyleRuntimeException) throwable).origin();
						if (origin instanceof Break) {
							break;
						} else if (origin instanceof Continue) {
							continue;
						} else if (origin instanceof Remove) {
							it.remove();
						} else {
							throw ((StyleRuntimeException) throwable);
						}
					} else {
						throw style.$(throwable);
					}
				}
			}
		}

		public void forThose(R2ArgsInterface<Boolean, K, V> predicate, Void2ArgInterface<K, V> func) {
			forThose(predicate, style.$(func));
		}

		public void forThose(R2ArgsInterface<Boolean, K, V> predicate, function<Object> func) {
			Iterator<K> it = map.keySet().iterator();
			while (it.hasNext()) {
				K k = it.next();
				V v = map.get(k);
				try {
					if (predicate.apply(k, v))
						func.apply(k, v);
				} catch (Throwable throwable) {
					if (throwable instanceof StyleRuntimeException) {
						Throwable origin = ((StyleRuntimeException) throwable).origin();
						if (origin instanceof Break) {
							break;
						} else if (origin instanceof Continue) {
							continue;
						} else if (origin instanceof Remove) {
							it.remove();
						} else {
							throw ((StyleRuntimeException) throwable);
						}
					} else {
						throw style.$(throwable);
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
				return via(style.$(method));
			}

			public Coll via(function<R> method) {
				for (K key : map.keySet()) {
					try {
						R val = method.apply(key, map.get(key));
						collection.add(val);
					} catch (Throwable e) {
						if (e instanceof StyleRuntimeException) {
							Throwable origin = ((StyleRuntimeException) e).origin();
							if (origin instanceof Break) {
								break;
							} else if (origin instanceof Continue) {
								continue;
							} else {
								throw ((StyleRuntimeException) e);
							}
						} else {
							throw style.$(e);
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
				return via(style.$(method));
			}

			public M via(function<Entry<K2, V2>> method) {
				for (K key : map.keySet()) {
					try {
						Entry<K2, V2> val = method.apply(key, map.get(key));
						toMap.put(val.key, val.value);
					} catch (Throwable e) {
						if (e instanceof StyleRuntimeException) {
							Throwable origin = ((StyleRuntimeException) e).origin();
							if (origin instanceof Break) {
								break;
							} else if (origin instanceof Continue) {
								continue;
							} else {
								throw ((StyleRuntimeException) e);
							}
						} else {
							throw style.$(e);
						}
					}
				}
				return toMap;
			}
		}

		public <K2, V2, M extends Map<K2, V2>> TransformerMap<K, V, K2, V2, M> to(M m) {
			return new TransformerMap<K, V, K2, V2, M>(map, m);
		}

		public V $(K key) {
			return map.get(key);
		}
	}

	public static class function<R> {

		private VoidNArgInterface voidN;
		private Void0ArgInterface void0;
		private Void1ArgInterface<Object> void1;
		private Void2ArgInterface<Object, Object> void2;
		private Void3ArgInterface<Object, Object, Object> void3;
		private Void4ArgInterface<Object, Object, Object, Object> void4;
		private Void5ArgInterface<Object, Object, Object, Object, Object> void5;
		private Void6ArgInterface<Object, Object, Object, Object, Object, Object> void6;
		private Void7ArgInterface<Object, Object, Object, Object, Object, Object, Object> void7;

		private RNArgsInterface<R> body;
		private R0ArgInterface<R> body0;
		private R1ArgInterface<R, Object> body1;
		private R2ArgsInterface<R, Object, Object> body2;
		private R3ArgsInterface<R, Object, Object, Object> body3;
		private R4ArgsInterface<R, Object, Object, Object, Object> body4;
		private R5ArgsInterface<R, Object, Object, Object, Object, Object> body5;
		private R6ArgsInterface<R, Object, Object, Object, Object, Object, Object> body6;
		private R7ArgsInterface<R, Object, Object, Object, Object, Object, Object, Object> body7;

		function(VoidNArgInterface body) {
			this.voidN = body;
		}

		function(Void0ArgInterface body) {
			this.void0 = body;
		}

		@SuppressWarnings("unchecked")
		function(Void1ArgInterface<?> body) {
			this.void1 = (Void1ArgInterface<Object>) body;
		}

		@SuppressWarnings("unchecked")
		function(Void2ArgInterface<?, ?> body) {
			this.void2 = (Void2ArgInterface<Object, Object>) body;
		}

		@SuppressWarnings("unchecked")
		function(Void3ArgInterface<?, ?, ?> body) {
			this.void3 = (Void3ArgInterface<Object, Object, Object>) body;
		}

		@SuppressWarnings("unchecked")
		function(Void4ArgInterface<?, ?, ?, ?> body) {
			this.void4 = (Void4ArgInterface<Object, Object, Object, Object>) body;
		}

		@SuppressWarnings("unchecked")
		function(Void5ArgInterface<?, ?, ?, ?, ?> body) {
			this.void5 = (Void5ArgInterface<Object, Object, Object, Object, Object>) body;
		}

		@SuppressWarnings("unchecked")
		function(Void6ArgInterface<?, ?, ?, ?, ?, ?> body) {
			this.void6 = (Void6ArgInterface<Object, Object, Object, Object, Object, Object>) body;
		}

		@SuppressWarnings("unchecked")
		function(Void7ArgInterface<?, ?, ?, ?, ?, ?, ?> body) {
			this.void7 = (Void7ArgInterface<Object, Object, Object, Object, Object, Object, Object>) body;
		}

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
					if (body0 == null) {
						void0.invoke();
						return null;
					} else {
						return body0.apply();
					}
				} else if (args.length == 1) {
					if (body1 == null) {
						void1.accept(args[0]);
						return null;
					} else {
						return body1.apply(args[0]);
					}
				} else if (args.length == 2) {
					if (body2 == null) {
						void2.accept(args[0], args[1]);
						return null;
					} else {
						return body2.apply(args[0], args[1]);
					}
				} else if (args.length == 3) {
					if (body3 == null) {
						void3.accept(args[0], args[1], args[2]);
						return null;
					} else {
						return body3.apply(args[0], args[1], args[2]);
					}
				} else if (args.length == 4) {
					if (body4 == null) {
						void4.accept(args[0], args[1], args[2], args[3]);
						return null;
					} else {
						return body4.apply(args[0], args[1], args[2], args[3]);
					}
				} else if (args.length == 5) {
					if (body5 == null) {
						void5.accept(args[0], args[1], args[2], args[3], args[4]);
						return null;
					} else {
						return body5.apply(args[0], args[1], args[2], args[3], args[4]);
					}
				} else if (args.length == 6) {
					if (body6 == null) {
						void6.accept(args[0], args[1], args[2], args[3], args[4], args[5]);
						return null;
					} else {
						return body6.apply(args[0], args[1], args[2], args[3], args[4], args[5]);
					}
				} else if (args.length == 7) {
					if (body7 == null) {
						void7.accept(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
						return null;
					} else {
						return body7.apply(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
					}
				} else {
					if (body == null) {
						voidN.accept(args);
						return null;
					} else {
						return body.invoke(args);
					}
				}
			} catch (Throwable t) {
				throw $(t);
			}
		}

		public Async<R> async(Object... args) {
			return new Async<>(this, args);
		}
	}

	public static class AsyncGroup {
		private Async<?>[] group;

		AsyncGroup(Async<?>... group) {
			if (group.length == 0)
				throw new IllegalArgumentException("at least one Async object should be passed in");
			this.group = Arrays.copyOf(group, group.length);
		}

		public void callback(function<Object> func) {
			try {
				Object[] awaits = new Object[group.length];
				int i = 0;
				for (Async<?> async : group) {
					awaits[i] = async.await();
					++i;
				}
				func.apply(awaits);
			} catch (Throwable e) {
				throw $(e);
			}
		}
	}

	public static class StyleRuntimeException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3062353717153173710L;

		public StyleRuntimeException(Throwable t) {
			super(t);
		}

		public void throwIn(@SuppressWarnings("unchecked") Class<? extends Throwable>... classes) {
			for (Class<? extends Throwable> cls : classes) {
				if (cls.isInstance(getCause())) {
					throw this;
				}
			}
		}

		public void throwNotIn(@SuppressWarnings("unchecked") Class<? extends Throwable>... classes) {
			boolean toThrow = true;
			for (Class<? extends Throwable> cls : classes) {
				if (cls.isInstance(getCause())) {
					toThrow = false;
					break;
				}
			}
			if (toThrow) {
				throw this;
			}
		}

		public Throwable origin() {
			return super.getCause();
		}

		@Override
		public Throwable getCause() {
			Throwable target = super.getCause();
			if (target instanceof InvocationTargetException) {
				return ((InvocationTargetException) target).getTargetException();
			} else {
				return target.getCause();
			}
		}
	}

	public static class SwitchBlock<T> {
		private T toSwitch;
		private boolean doNext = false;

		SwitchBlock(T t) {
			this.toSwitch = t;
		}

		public SwitchBlock<T> Case(T ca, function<?> func) {
			if (toSwitch.equals(ca) || doNext) {
				try {
					func.apply();
					doNext = true;
				} catch (Throwable t) {
					if (t instanceof StyleRuntimeException) {
						if (((StyleRuntimeException) t).origin() instanceof Break) {
							doNext = false;
						}
					} else {
						throw $(t);
					}
				}
			}
			return this;
		}

		public SwitchBlock<T> Case(T ca, Void0ArgInterface func) {
			return Case(ca, $(func));
		}
	}

	public static class JSONLike<K, V> extends LinkedHashMap<K, V> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7448591337510287830L;

		public JSONLike(K key, V value) {
			put(key, value);
		}

		public JSONLike<K, V> $(K key, V value) {
			put(key, value);
			return this;
		}
	}
}
