package net.cassite.pure.data;

/**
 * Created by wkgcass on 15/10/11.
 */
public class DataComparable<T extends Comparable<T>> extends ParameterComparable implements IData<T>, Comparable<T> {
    private T item;
    final Object entity;

    public DataComparable(Object entity) {
        this.item = null;
        this.entity = entity;
    }

    public DataComparable(T item, Object entity) {
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
        return DataUtils.dataToStringUtil(this);
    }

    @Override
    public int compareTo(T o) {
        return item.compareTo(o);
    }

    @Override
    public UpdateEntry as(Object o) {
        return new UpdateEntry(this, o);
    }
}
