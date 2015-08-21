package net.cassite.style;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

import net.cassite.style.control.*;

public class IterableFuncSup<T> implements A1FuncSup<T> {
	protected final Iterable<T> iterable;

	IterableFuncSup(Iterable<T> iterable) {
		this.iterable = iterable;
	}

	public <R> R forThose(Predicate<T> predicate, def<R> func) {
		Iterator<T> it = iterable.iterator();
		ptr<Integer> i = ptr(0);
		IteratorInfo<R> info = new IteratorInfo<R>();
		return While(() -> it.hasNext(), (res) -> {
			T t = it.next();
			try {
				if (predicate.test(t))
					if (func.argCount() == 2)
						return func.apply(t,
								info.setValues(i.item - 1, i.item + 1, i.item == 0, it.hasNext(), i.item, res));
					else
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
			} finally {
				i.item += 1;
			}
			return null;
		});
	}

	@Override
	public T first() {
		return iterable.iterator().next();
	}

	public <R, Coll extends Collection<R>> Tramsformer<R, T, Coll> to(Coll collection) {
		return new Tramsformer<>(iterable, collection);
	}

	public static class Tramsformer<R, T, Coll extends Collection<R>> implements A1Transformer<R, T, Coll> {
		protected final Coll collection;
		protected final Iterable<T> iterable;

		Tramsformer(Iterable<T> iterable, Coll collection) {
			this.iterable = iterable;
			this.collection = collection;
		}

		@Override
		public Coll via(def<R> method) {
			$(iterable).forEach(e -> {
				collection.add(method.apply(e));
			});
			return collection;
		}
	}

	@Override
	public <Coll extends Collection<T>> Coll findAll(def<Boolean> filter, Coll toColl, int limit) {
		return $(iterable).to(toColl).via(e -> {
			if (!filter.apply(e))
				Continue();
			if (limit > 0 && limit <= toColl.size()) {
				Break();
			}
			return e;
		});
	}
}