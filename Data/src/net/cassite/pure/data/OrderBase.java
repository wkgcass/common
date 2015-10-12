package net.cassite.pure.data;

public class OrderBase {
    public final OrderType type;
    public final IData<?> data;

    public OrderBase(OrderType type, IData<?> data) {
        this.type = type;
        this.data = data;
    }
}
