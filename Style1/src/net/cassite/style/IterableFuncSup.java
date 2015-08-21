package net.cassite.style;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

import net.cassite.style.interfaces.*;
import net.cassite.style.control.*;

public class IterableFuncSup<T> extends Style {
	protected final Iterable<T> iterable;

	IterableFuncSup(Iterable<T> iterable) {
		this.iterable = iterable;
	}

	@SuppressWarnings("unchecked")
	public <R> R forEach(VFunc1<T> func) {
		return (R) forEach($(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forEach(VFunc2<T, IteratorInfo<R>> func) {
		return (R) forEach($(func));
	}

	public <R> R forEach(RFunc1<R, T> func) {
		return forEach($(func));
	}

	public <R> R forEach(RFunc2<R, T, IteratorInfo<R>> func) {
		return forEach($(func));
	}

	public <R> R forEach(def<R> func) {
		return (R) forThose($.alwaysTrue(), func);
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(Predicate<T> predicate, VFunc1<T> func) {
		return (R) forThose(predicate, $(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(Predicate<T> predicate, VFunc2<T, IteratorInfo<R>> func) {
		return (R) forThose(predicate, $(func));
	}

	public <R> R forThose(Predicate<T> predicate, RFunc1<R, T> func) {
		return forThose(predicate, $(func));
	}

	public <R> R forThose(Predicate<T> predicate, RFunc2<R, T, IteratorInfo<R>> func) {
		return forThose(predicate, $(func));
	}

	public <R> R forThose(Predicate<T> predicate, def<R> func) {
		Iterator<T> it = iterable.iterator();
		if (func.argCount() == 2) {
			ptr<Integer> i = ptr(0);
			IteratorInfo<R> info = new IteratorInfo<R>();
			return While(() -> it.hasNext(), (res) -> {
				T t = it.next();
				try {
					if (predicate.test(t))
						return func.apply(t,
								info.setValues(i.item - 1, i.item + 1, i.item == 0, it.hasNext(), i.item, res));
					else
						return null;
				} catch (Throwable err) {
					StyleRuntimeException sErr = $(err);
					Throwable throwable = sErr.origin();
					if (throwable instanceof Remove) {
						it.remove();
					} else {
						throw sErr;
					}
				} finally {
					i.item += 1;
				}
				return null;
			});
		} else {
			return While(() -> it.hasNext(), () -> {
				T t = it.next();
				try {
					if (predicate.test(t))
						return func.apply(t);
					else
						return null;
				} catch (Throwable err) {
					StyleRuntimeException sErr = $(err);
					Throwable throwable = sErr.origin();
					if (throwable instanceof Remove) {
						it.remove();
					} else {
						throw sErr;
					}
				}
				return null;
			});
		}
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
			$(iterable).forEach(e -> {
				collection.add(method.apply(e));
			});
			return collection;
		}
	}
}