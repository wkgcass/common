package net.cassite.pure.data;

/**
 * 排序依据
 */
public class OrderBase {
    /**
     * 排序依据,定义在OrderType中
     *
     * @see OrderType
     */
    public final OrderType type;
    /**
     * 排序字段
     */
    public final IData<?> data;

    OrderBase(OrderType type, IData<?> data) {
        this.type = type;
        this.data = data;
    }
}
