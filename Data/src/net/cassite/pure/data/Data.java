package net.cassite.pure.data;

import java.lang.reflect.Field;

public class Data<T> extends Parameter implements IData<T> {
    protected T item;
    final Object entity;

    public Data(Object entity) {
        this.item = null;
        this.entity = entity;
    }

    public Data(T item, Object entity) {
        this.item = item;
        this.entity = entity;
    }

    @Override
    public T get() {
        return item;
    }

    @Override
    public void set(T item) {
        this.item = item;
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
        return DataUtils.toStringUtil(this);
    }

}
