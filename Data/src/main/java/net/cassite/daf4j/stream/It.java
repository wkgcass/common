package net.cassite.daf4j.stream;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 用于根据List[Map]返回Iterator
 *
 * @see QueryIntStream#iterator()
 */
class It<E> implements Iterator<E> {
        private Iterator<Map<String, Object>> it;
        private String alias;

        It(List<Map<String, Object>> resList, String alias) {
                this.it = resList.iterator();
                this.alias = alias;
        }

        @Override
        public boolean hasNext() {
                return it.hasNext();
        }

        @SuppressWarnings("unchecked")
        @Override
        public E next() {
                return (E) it.next().get(alias);
        }

        @Override
        public void remove() {
                throw new UnsupportedOperationException();
        }
}
