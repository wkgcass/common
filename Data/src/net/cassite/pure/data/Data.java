package net.cassite.pure.data;

public class Data<T> extends Parameter {
        private T item;
        final Object entity;

        public Data(Object entity) {
                this.item = null;
                this.entity = entity;
        }

        public Data(T item, Object entity) {
                this.item = item;
                this.entity = entity;
        }

        public T get() {
                return item;
        }

        public void set(T item) {
                this.item = item;
        }

        public OrderBase desc() {
                return new OrderBase(OrderType.desc, this);
        }

        public OrderBase asc() {
                return new OrderBase(OrderType.asc, this);
        }

}
