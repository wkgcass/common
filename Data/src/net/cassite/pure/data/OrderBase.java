package net.cassite.pure.data;

public class OrderBase {
        public final OrderType type;
        public final Data<?> data;

        public OrderBase(OrderType type, Data<?> data) {
                this.type = type;
                this.data = data;
        }
}
