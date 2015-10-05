package net.cassite.style;

public class Entry<K, V> implements java.util.Map.Entry<K, V> {
        public Entry() {
        }

        public Entry(K key, V value) {
                this.key = key;
                this.value = value;
        }

        public K key;
        public V value;

        @Override
        public K getKey() {
                return key;
        }

        @Override
        public V getValue() {
                return value;
        }

        @Override
        public V setValue(V value) {
                V original = this.value;
                this.value = value;
                return original;
        }
}