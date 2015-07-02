package cass.pure.persist.support;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import cass.pure.persist.Entity;
import cass.pure.persist.EntityManager;

public class LazyPersistenceList<E extends Entity> implements Set<E>, List<E> {

	private List<String> list;
	private Map<Integer, E> entities = new HashMap<Integer, E>();

	private Class<?> entityClass;

	public class LazyPersistenceIterator<T extends Entity> implements
			ListIterator<T> {
		private List<T> backedList;
		private int cursor = 0;

		public LazyPersistenceIterator(List<T> backedList) {
			this(backedList, 0);
		}

		public LazyPersistenceIterator(List<T> backedList, int cursor) {
			this.backedList = backedList;
			this.cursor = cursor;
		}

		@Override
		public boolean hasNext() {
			return cursor < list.size();
		}

		@Override
		public T next() {
			T t = backedList.get(cursor);
			++cursor;
			return t;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasPrevious() {
			return cursor >= 0;
		}

		@Override
		public T previous() {
			--cursor;
			return backedList.get(cursor);
		}

		@Override
		public int nextIndex() {
			return cursor;
		}

		@Override
		public int previousIndex() {
			return cursor - 1;
		}

		@Override
		public void set(T e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(T e) {
			throw new UnsupportedOperationException();
		}
	}

	public LazyPersistenceList(List<String> pkValueList, Class<?> entityClass)
			throws SQLException {
		this.list = pkValueList;
		this.entityClass = entityClass;
	}

	private E parse(int i) {
		E e = EntityManager.getEntity(entityClass, list.get(i));
		entities.put(i, e);
		return e;
	}

	@Override
	public boolean add(E arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object arg0) {
		if (!(arg0 instanceof Entity))
			return false;
		for (int i = 0; i < list.size(); ++i) {
			if (get(i).equals(arg0))
				return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		for (Object o : arg0) {
			if (!this.contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return new LazyPersistenceIterator<E>(this);
	}

	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public Object[] toArray() {
		Object[] ret = new Object[this.size()];
		for (int i = 0; i < this.size(); ++i) {
			ret[i] = this.get(i);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] arg0) {
		if (arg0.length < this.size()) {
			arg0 = (T[]) Array.newInstance(arg0.getClass(), this.size());
		}
		for (int i = 0; i < this.size(); ++i) {
			arg0[i] = (T) this.get(i);
		}
		for (int i = this.size(); i < arg0.length; ++i) {
			arg0[i] = null;
		}
		return arg0;
	}

	@Override
	public void add(int arg0, E arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends E> arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E get(int arg0) {
		if (entities.containsKey(arg0)) {
			return entities.get(arg0);
		} else {
			E entity = parse(arg0);
			return entity;
		}
	}

	@Override
	public int indexOf(Object arg0) {
		for (int i = 0; i < this.size(); ++i) {
			if (this.get(i).equals(arg0))
				return i;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object arg0) {
		for (int i = this.size() - 1; i >= 0; --i) {
			if (this.get(i).equals(arg0)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public ListIterator<E> listIterator() {
		return new LazyPersistenceIterator<E>(this);
	}

	@Override
	public ListIterator<E> listIterator(int arg0) {
		return new LazyPersistenceIterator<E>(this, arg0);
	}

	@Override
	public E remove(int arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E set(int arg0, E arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<E> subList(int arg0, int arg1) {
		List<E> ret = new ArrayList<E>(arg1 - arg0);
		for (int i = arg0; i < arg1; ++i) {
			ret.add(this.get(i));
		}
		return ret;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		if (size() != 0)
			sb.append(get(0));
		for (int i = 1; i < size(); ++i) {
			sb.append(", ");
			sb.append(get(i));
		}
		sb.append("]");
		return sb.toString();
	}

}