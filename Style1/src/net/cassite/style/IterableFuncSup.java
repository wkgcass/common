package net.cassite.style;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

import net.cassite.style.interfaces.RFunc1;
import net.cassite.style.interfaces.VFunc1;
import net.cassite.style.interfaces.VFunc2;
import net.cassite.style.control.*;

public class IterableFuncSup<T> extends Style {
	public final Iterable<T> iterable;

	IterableFuncSup(Iterable<T> iterable) {
		this.iterable = iterable;
	}

	@SuppressWarnings("unchecked")
	public <R> R forEach(VFunc1<T> func) {
		return (R) forEach($(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forEach(VFunc2<T, IteratorInfo> func) {
		return (R) forEach($(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forEach(def<Object> func) {
		return (R) forThose($.alwaysTrue(), func);
	}

	public <R> R forThose(Predicate<T> predicate, VFunc1<T> func) {
		return forThose(predicate, $(func));
	}

	public <R> R forThose(Predicate<T> predicate, VFunc2<T, IteratorInfo> func) {
		return forThose(predicate, $(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(Predicate<T> predicate, def<Object> func) {
		Iterator<T> it = iterable.iterator();
		int i = 0;
		R res = null;
		while (it.hasNext()) {
			try {
				T t = it.next();
				if (predicate.test(t))
					if (func.argCount() == 2)
						func.apply(t, new IteratorInfo(i - 1, i + 1, i == 0, it.hasNext(), i));
					else
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
					} else if (origin instanceof BreakWithResult) {
						res = (R) ((BreakWithResult) origin).getRes();
						break;
					} else {
						throw ((StyleRuntimeException) throwable);
					}
				} else {
					throw $(throwable);
				}
			} finally {
				++i;
			}
		}
		return res;
	}

	public T first() {
		return iterable.iterator().next();
	}

	public <R, Coll extends Collection<R>> Tramsformer<R, T, Coll> to(Coll collection) {
		return new Tramsformer<>(iterable, collection);
	}

	public static class Tramsformer<R, T, Coll extends Collection<R>> {
		protected final Coll collection;
		protected final Iterable<T> iterable;

		Tramsformer(Iterable<T> iterable, Coll collection) {
			this.iterable = iterable;
			this.collection = collection;
		}

		public Coll via(RFunc1<R, T> method) {
			return via($(method));
		}

		public Coll via(def<R> method) {
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