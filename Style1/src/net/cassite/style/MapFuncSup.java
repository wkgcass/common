package net.cassite.style;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.cassite.style.interfaces.RFunc2;
import net.cassite.style.interfaces.VFunc2;
import net.cassite.style.interfaces.VFunc3;
import net.cassite.style.control.*;

public class MapFuncSup<K, V> {
	private Map<K, V> map;

	MapFuncSup(Map<K, V> map) {
		this.map = map;
	}

	public Object forEach(VFunc2<K, V> func) {
		return forEach(Style.$(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forEach(def<Object> func) {
		return (R) forThose((k, v) -> true, func);
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(RFunc2<Boolean, K, V> predicate, VFunc2<K, V> func) {
		return (R) forThose(predicate, Style.$(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(RFunc2<Boolean, K, V> predicate, VFunc3<K, V, IteratorInfo> func) {
		return (R) forThose(predicate, Style.$(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(RFunc2<Boolean, K, V> predicate, def<R> func) {
		Iterator<K> it = map.keySet().iterator();
		int i = 0;
		R res = null;
		while (it.hasNext()) {
			K k = it.next();
			V v = map.get(k);
			try {
				if (predicate.apply(k, v))
					if (func.argCount() == 3)
						func.apply(k, v, new IteratorInfo(i - 1, i + 1, i != 0, it.hasNext(), i));
					else
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
					} else if (origin instanceof BreakWithResult) {
						res = (R) ((BreakWithResult) origin).getRes();
						break;
					} else {
						throw ((StyleRuntimeException) throwable);
					}
				} else {
					throw Style.$(throwable);
				}
			} finally {
				++i;
			}
		}
		return res;
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

		public Coll via(RFunc2<R, K, V> method) {
			return via(Style.$(method));
		}

		public Coll via(def<R> method) {
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
						throw Style.$(e);
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

		public M via(RFunc2<Entry<K2, V2>, K, V> method) {
			return via(Style.$(method));
		}

		public M via(def<Entry<K2, V2>> method) {
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
						throw Style.$(e);
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