package net.cassite.style;

import net.cassite.style.interfaces.RFunc1;
import net.cassite.style.interfaces.VFunc1;
import net.cassite.style.interfaces.VFunc2;

import java.util.Collection;
import java.util.function.Predicate;

import net.cassite.style.control.Break;
import net.cassite.style.control.BreakWithResult;
import net.cassite.style.control.Continue;

public class ArrayFuncSup<T> extends Style {
	private final T[] array;

	ArrayFuncSup(T[] array) {
		this.array = array;
	}

	public <R> R forEach(VFunc1<T> func) {
		return forEach($(func));
	}

	public <R> R forEach(VFunc2<T, IteratorInfo> func) {
		return forEach($(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forEach(def<Object> func) {
		return (R) forThose($.alwaysTrue(), func);
	}

	public void toSelf(RFunc1<T, T> func) {
		toSelf($(func));
	}

	public void toSelf(def<T> func) {
		for (int i = 0; i < array.length; ++i) {
			try {
				array[i] = func.apply(array[i]);
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

	@SuppressWarnings("unchecked")
	public <R> R forThose(Predicate<T> predicate, VFunc1<T> func) {
		return (R) forThose(predicate, $(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(Predicate<T> predicate, VFunc2<T, IteratorInfo> func) {
		return (R) forThose(predicate, $(func));
	}

	@SuppressWarnings("unchecked")
	public <R> R forThose(Predicate<T> predicate, def<R> func) {
		int i = 0;
		R res = null;
		for (T t : array) {
			try {
				if (predicate.test(t)) {
					R tmpRes;
					if (func.argCount() == 2)
						tmpRes = func.apply(t, new IteratorInfo(i - 1, i + 1, i != 0, i != array.length - 1, i));
					else
						tmpRes = func.apply(t);
					if (tmpRes != null) {
						res = tmpRes;
					}
				}
			} catch (Throwable throwable) {
				if (throwable instanceof StyleRuntimeException) {
					Throwable origin = ((StyleRuntimeException) throwable).origin();
					if (origin instanceof Break) {
						break;
					} else if (origin instanceof Continue) {
						continue;
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

		public Coll via(RFunc1<R, T> method) {
			return via($(method));
		}

		public Coll via(def<R> method) {
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