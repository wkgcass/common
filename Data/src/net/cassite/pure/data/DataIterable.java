package net.cassite.pure.data;

import net.cassite.pure.data.util.DataUtils;

/**
 * Created by wkgcass on 15/10/11.
 */
public class DataIterable<E, T extends Iterable<E>> extends ParameterAggregate implements IData<T> {
    private T item;
    public final Object entity;

    public DataIterable(T it, Object entity) {
        this.item = it;
        this.entity = entity;
    }

    @Override
    public T get() {
        return item;
    }

    @Override
    public void set(T t) {
        this.item = t;
    }

    @Override
    public Object getEntity() {
        return entity;
    }

    @Override
    public OrderBase desc() {
        return new OrderBase(OrderType.desc, this);
    }

    @Override
    public OrderBase asc() {
        return new OrderBase(OrderType.asc, this);
    }

    @Override
    public String toString() {
        return DataUtils.dataToStringUtil(this);
    }
}
