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
		ListIterator<T> it = ((List<T>) iterable).listIterator();
		while (it.hasNext()) {
			T t = it.next();
			try {
				it.set(func.apply(t));
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

	public <R> R forEach(VFunc1<T> func) {
		return forEach($(func));
	}

	public <R> R forEach(VFunc1<T> func, int index) {
		return forEach($(func), index);
	}

	@SuppressWarnings("unchecked")
	public <R> R forEach(def<Object> func, int index) {
		return (R) forThose($.alwaysTrue(), func, index);
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(Predicate<T> predicate, VFunc1<T> func, int index) {
		return (R) forThose(predicate, $(func), index);
	}

	public <R> R forThose(Predicate<T> predicate, VFunc2<T, IteratorInfo> func) {
		return forThose(predicate, $(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(Predicate<T> predicate, VFunc2<T, IteratorInfo> func, int index) {
		return (R) forThose(predicate, $(func), index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R forThose(Predicate<T> predicate, def<Object> func) {
		return (R) forThose(predicate, func, 0);
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(Predicate<T> predicate, def<R> func, int index) {
		ListIterator<T> it = ((List<T>) iterable).listIterator(index);
		T t;
		if (it.hasNext()) {
			t = it.next();
		} else {
			t = null;
		}
		R res = null;
		while (it.hasNext()) {
			try {
				if (predicate.test(t))
					if (func.argCount() == 2) {
						IteratorInfo info = new IteratorInfo(it.previousIndex(), it.nextIndex(), it.hasPrevious(),
								it.hasNext(), it.previousIndex() + 1);
						func.apply(t, info);
					} else
						func.apply(t);
				t = it.next();
			} catch (Throwable e) {
				if (e instanceof StyleRuntimeException) {
					Throwable origin = ((StyleRuntimeException) e).origin();
					if (origin instanceof Break) {
						break;
					} else if (origin instanceof Continue) {
						t = it.next();
						continue;
					} else if (origin instanceof Add) {
						it.add(((Add) origin).getAdd());
					} else if (origin instanceof Previous) {
						t = it.previous();
					} else if (origin instanceof $Set) {
						it.set((($Set) origin).getSet());
						t = it.next();
					} else if (origin instanceof BreakWithResult) {
						res = (R) ((BreakWithResult) origin).getRes();
						break;
					} else {
						throw ((StyleRuntimeException) e);
					}
				} else {
					throw $(e);
				}
			}
		}
		return res;
	}
}