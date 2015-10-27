package net.cassite.daf4j;

/**
 * 用于实体中的普通类型的数据项.<br>
 * 对于日期或数字类型(可比较的类型)应当使用DataComparable以获取更多支持<br>
 * 对于聚合类型应当使用DataIterable
 *
 * @param <T> 存储的数据类型
 * @see DataComparable
 * @see DataIterable
 */
public class Data<T> extends Parameter implements IDataAssignable<T> {
    protected T item;
    /**
     * 该数据项所在实体
     */
    public final Object entity;

    /**
     * 将该数据项初始化为空值
     *
     * @param entity 该数据项所在的实体,使用(this)填入该参数
     */
    public Data(Object entity) {
        this.item = null;
        this.entity = entity;
    }

    /**
     * 将该数据项初始化为指定值
     *
     * @param item   指定的初始化值
     * @param entity 该数据线所在的实体,使用(this)填入该参数
     */
    public Data(T item, Object entity) {
        this.item = item;
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
     * @param item 要设置成的值
     */
    void set(T item) {
        this.item = item;
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
    public UpdateEntry as(Object o) {
        return new UpdateEntry(this, o);
    }

    @Override
    public String toString() {
        return DataUtils.dataToStringUtil(this);
    }

}
