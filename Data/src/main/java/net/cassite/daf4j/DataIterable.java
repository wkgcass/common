package net.cassite.daf4j;

/**
 * 用于实体中的聚合类型的数据项.<br>
 * 对于日期或数字类型(可比较的类型)应当使用DataComparable以获取更多支持<br>
 * 对于普通类型应当使用Data
 *
 * @param <E> 存储的元素类型
 * @param <T> 存储的数据类型(Iterable子类)
 */
public class DataIterable<E, T extends Iterable<E>> extends ParameterAggregate implements IData<T> {
    private T item;
    /**
     * 该数据项所在实体
     */
    public final Object entity;

    /**
     * 将该数据项初始化为指定值
     *
     * @param it     指定的初始化值
     * @param entity 该数据线所在的实体,使用(this)填入该参数
     */
    public DataIterable(T it, Object entity) {
        this.item = it;
        this.entity = entity;
    }

    @Override
    public T get() {
        return item;
    }

    /**
     * 设置数据值<br>
     * 在实体中应当通过DataUtils.set(_,_)完成该操作
     *
     * @param t 要设置成的值
     */
    void set(T t) {
        this.item = t;
    }

    @Override
    public Object getEntity() {
        return entity;
    }

    @Override
    public OrderBase desc() {
        return new OrderBase(OrderTypes.desc, this);
    }

    @Override
    public OrderBase asc() {
        return new OrderBase(OrderTypes.asc, this);
    }

    @Override
    public String toString() {
        return DataUtils.dataToStringUtil(this);
    }
}
