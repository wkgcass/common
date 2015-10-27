package net.cassite.daf4j;

/**
 * 可放入where中作为条件一部分的参数<br>
 * e.g. name.$eq('cass')
 */
public abstract class Parameter {

    /**
     * 该字段与给定值相等
     *
     * @param obj 要判断相等的对象
     * @return 条件
     */
    public Condition $eq(Object obj) {
        return new Condition(this, ConditionTypes.eq, new Object[]{obj});
    }

    /**
     * 该字段与给定值不相等
     *
     * @param obj 要判断不相等的对象
     * @return 条件
     */
    public Condition $ne(Object obj) {
        return new Condition(this, ConditionTypes.ne, new Object[]{obj});
    }

    /**
     * 该字段存在于子查询中
     *
     * @param query 子查询
     * @return 条件
     */
    public Condition in(PreResult<?> query) {
        return new Condition(this, ConditionTypes.in, new Object[]{query});
    }

    /**
     * 该字段不存在于子查询中
     *
     * @param query 子查询
     * @return 条件
     */
    public Condition notIn(PreResult<?> query) {
        return new Condition(this, ConditionTypes.notIn, new Object[]{query});
    }

    /**
     * 该字段与给定值相似<br>
     * e.g. sql中的like语句,应当用+将它们连接起来
     *
     * @param obj 给定值
     * @return 条件
     */
    public Condition like(Object... obj) {
        return new Condition(this, ConditionTypes.like, new Object[]{obj});
    }

    /**
     * 该字段是空的
     *
     * @return 条件
     */
    public Condition isNull() {
        return new Condition(this, ConditionTypes.isNull, new Object[0]);
    }

    /**
     * 该字段非空
     *
     * @return 条件
     */
    public Condition isNotNull() {
        return new Condition(this, ConditionTypes.isNotNull, new Object[0]);
    }

    /**
     * 该字段属于某个集合
     *
     * @param pc 要判断的集合
     * @return 条件
     */
    public Condition member(ParameterAggregate pc) {
        return new Condition(this, ConditionTypes.member, new Object[]{pc});
    }

    /**
     * 该字段不属于某个集合
     *
     * @param pc 要判断的集合
     * @return 条件
     */
    public Condition notMember(ParameterAggregate pc) {
        return new Condition(this, ConditionTypes.notMember, new Object[]{pc});
    }
}
