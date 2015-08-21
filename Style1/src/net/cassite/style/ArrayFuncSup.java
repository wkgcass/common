package net.cassite.style;

import net.cassite.style.interfaces.*;

import java.util.Collection;
import java.util.function.Predicate;

public class ArrayFuncSup<T> implements A1FuncSup<T> {
	private final T[] array;

	ArrayFuncSup(T[] array) {
		this.array = array;
	}

	public <R> R forEach(RFunc1<R, T> func) {
		return forEach($(func));
	}

	public <R> R forEach(RFunc2<R, T, IteratorInfo<R>> func) {
		return forEach($(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forEach(VFunc1<T> func) {
		return forEach((def<R>) $(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forEach(VFunc2<T, IteratorInfo<R>> func) {
		return forEach((def<R>) $(func));
	}

	public <R> R forEach(def<R> func) {
		return forThose($.alwaysTrue(), func);
	}

	public void toSelf(RFunc1<T, T> func) {
		toSelf($(func));
	}

	public void toSelf(def<T> func) {
		$(array).forEach((e, i) -> {
			array[$(i)] = func.apply(e);
		});
	}

	public <R> R forThose(Predicate<T> predicate, RFunc1<R, T> func) {
		return forThose(predicate, $(func));
	}

	public <R> R forThose(Predicate<T> predicate, RFunc2<R, T, IteratorInfo<R>> func) {
		return forThose(predicate, $(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(Predicate<T> predicate, VFunc1<T> func) {
		return (R) forThose(predicate, $(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(Predicate<T> predicate, VFunc2<T, IteratorInfo<R>> func) {
		return (R) forThose(predicate, $(func));
	}

	public <R> R forThose(Predicate<T> predicate, def<R> func) {
		if (array.length == 0)
			return null;
		IteratorInfo<R> info = new IteratorInfo<R>();
		return For(0).to(array.length - 1).loop((i, res) -> {
			if (predicate.test(array[i]))
				if (func.argCount() == 2)
					return func.apply(array[i], info.setValues(i - 1, i + 1, i != 0, i != array.length - 1, i, res));
				else
					return func.apply(array[i]);
			else
				return null;
		});
	}

	public T first() {
		return array[0];
	}

	public <R, Coll extends Collection<R>> Tramsformer<R, T, Coll> to(Coll collection) {
		return new Tramsformer<>(array, collection);
	}

	public static class Tramsformer<R, T, Coll extends Collection<R>> implements A1Transformer<R, T, Coll> {
		private final Coll collection;
		private final T[] array;

		Tramsformer(T[] array, Coll collection) {
			this.array = array;
			this.collection = collection;
		}

		public Coll via(def<R> method) {
			$(array).forEach(e -> {
				collection.add(method.apply(e));
			});
			return collection;
		}
	}

	public <Coll extends Collection<T>> Coll findAll(def<Boolean> filter, Coll toColl, int limit) {
		return $(array).to(toColl).via(e -> {
			if (!filter.apply(e))
				Continue();
			if (limit > 0 && limit <= toColl.size()) {
				Break();
			}
			return e;
		});
	}
}