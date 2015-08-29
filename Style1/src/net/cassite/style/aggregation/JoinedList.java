package net.cassite.style.aggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.cassite.style.Entry;

public class JoinedList<E> extends Aggregation implements List<E> {
        private List<List<E>> lists;

        @SafeVarargs
        public JoinedList(List<E>... toJoin) {
                lists = new ArrayList<List<E>>(toJoin.length);
                $(toJoin).forEach(list -> {
                        lists.add(list);
                });
        }

        @Override
        public int size() {
                return $(lists).forEach((list, i) -> {
                        return avoidNull(i.lastRes, 0) + list.size();
                });
        }

        @Override
        public boolean isEmpty() {
                return lists.isEmpty() ? true : $(lists).forEach(list -> {
                        if (!list.isEmpty())
                                BreakWithResult(false);
                        return true;
                });
        }

        @Override
        public boolean contains(Object o) {
                return lists.isEmpty() ? false : $(lists).forEach(list -> {
                        if (list.contains(o))
                                BreakWithResult(true);
                        return false;
                });
        }

        @Override
        public Iterator<E> iterator() {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public Object[] toArray() {
                Object[] arr = new Object[lists.isEmpty() ? 0 : (int) $(lists).forEach((e, i) -> {
                        return (Integer) avoidNull(i.lastRes, 0) + e.size();
                })];
                $(lists).forEach((list, i) -> {
                        return $(list).forEach((e, j) -> {
                                arr[(int) avoidNull(i.lastRes, 0) + (int) avoidNull(j.lastRes, 0)] = e;
                                return (int) avoidNull(i.lastRes, 0) + 1;
                        });
                });
                return arr;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T[] toArray(T[] arr) {
                $(lists).forEach((list, i) -> {
                        int res = $(list).forEach((e, j) -> {
                                int index = (int) avoidNull(i.lastRes, 0) + (int) avoidNull(j.lastRes, 0);
                                if (index >= arr.length)
                                        BreakWithResult(-1);
                                arr[index] = (T) e;
                                return (int) avoidNull(i.lastRes, 0) + 1;
                        });
                        return res == -1 ? Break() : res;
                });
                return arr;
        }

        @Override
        public boolean add(E e) {
                throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
                throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
                return lists.isEmpty() ? false : $(c).forEach(e -> {
                        if (!contains(e))
                                BreakWithResult(false);
                        return true;
                });
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
                throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
                throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
                throw new UnsupportedOperationException();
        }

        private Entry<List<E>, Integer> getListAndPos(int index) {
                int sumIndex = 0;
                int listIndex = -1;
                while (sumIndex < index) {
                        ++listIndex;
                        sumIndex += lists.get(listIndex).size();
                }
                return new Entry<List<E>, Integer>(lists.get(listIndex), index - sumIndex + lists.get(listIndex).size());
        }

        @Override
        public E get(int index) {
                Entry<List<E>, Integer> entry = getListAndPos(index);
                return entry.key.get(entry.value);
        }

        @Override
        public E set(int index, E element) {
                Entry<List<E>, Integer> entry = getListAndPos(index);
                return entry.key.set(entry.value, element);
        }

        @Override
        public void add(int index, E element) {
                throw new UnsupportedOperationException();
        }

        @Override
        public E remove(int index) {
                throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {
                return lists.size() == 0 ? -1 : $(lists).forEach((list, i) -> {
                        int indexJ = $(list).forEach((e, j) -> {
                                return e.equals(o) ? BreakWithResult($(j)) : -1;
                        });
                        return indexJ != -1 ? BreakWithResult(indexJ + i.lastRes) : -1;
                });
        }

        @Override
        public int lastIndexOf(Object o) {
                // TODO Auto-generated method stub
                return 0;
        }

        @Override
        public ListIterator<E> listIterator() {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public ListIterator<E> listIterator(int index) {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
                // TODO Auto-generated method stub
                return null;
        }

}
