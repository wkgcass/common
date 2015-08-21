package net.cassite.style;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

import net.cassite.style.interfaces.RFunc1;
import net.cassite.style.interfaces.VFunc1;
import net.cassite.style.interfaces.VFunc2;
import net.cassite.style.control.*;

public class ListFuncSup<T> extends CollectionFuncSup<T> {
	ListFuncSup(List<T> collection) {
		super(collection);
	}

	public void toSelf(RFunc1<T, T> func) {
		toSelf($(func));
	}

	public void toSelf(def<T> func) {
		$((List<T>) iterable).forEach(e -> {
			return Set(func.apply(e));
		});
	}

	@SuppressWarnings("unchecked")
	public <R> R forEach(VFunc1<T> func) {
		return (R) forEach($(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forEach(VFunc1<T> func, int index) {
		return (R) forEach($(func), index);
	}

	public <R> R forEach(def<R> func, int index) {
		return (R) forThose($.alwaysTrue(), func, index);
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(Predicate<T> predicate, VFunc1<T> func, int index) {
		return (R) forThose(predicate, $(func), index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R forThose(Predicate<T> predicate, VFunc2<T, IteratorInfo<R>> func) {
		return (R) forThose(predicate, $(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(Predicate<T> predicate, VFunc2<T, IteratorInfo<R>> func, int index) {
		return (R) forThose(predicate, $(func), index);
	}

	@Override
	public <R> R forThose(Predicate<T> predicate, def<R> func) {
		return (R) forThose(predicate, func, 0);
	}

	public <R> R forThose(Predicate<T> predicate, def<R> func, int index) {
		ListIterator<T> it = ((List<T>) iterable).listIterator(index);
		if (func.argCount() == 2) {
			IteratorInfo<R> info = new IteratorInfo<R>();
			return While(() -> it.hasNext(), (res) -> {
				T t = it.next();
				try {
					if (predicate.test(t))
						return func.apply(t, info.setValues(it.previousIndex(), it.nextIndex(), it.hasPrevious(),
								it.hasNext(), it.previousIndex() + 1, res));
					else
						return null;
				} catch (Throwable err) {
					StyleRuntimeException sErr = $(err);
					Throwable throwable = sErr.origin();
					if (throwable instanceof Add) {
						it.add(((Add) throwable).getAdd());
					} else if (throwable instanceof $Set) {
						it.set((($Set) throwable).getSet());
					} else if (throwable instanceof Remove) {
						it.remove();
					} else {
						throw sErr;
					}
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
					if (throwable instanceof Add) {
						it.add(((Add) throwable).getAdd());
					} else if (throwable instanceof $Set) {
						it.set((($Set) throwable).getSet());
					} else {
						throw sErr;
					}
				}
				return null;
			});
		}
	}
}