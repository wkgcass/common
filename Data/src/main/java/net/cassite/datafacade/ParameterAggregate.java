package net.cassite.datafacade;

/**
 * 可放入where的条件的一部分的集合型参数
 */
public abstract class ParameterAggregate extends Parameter {
    /**
     * 将参数本身置于member之后
     *
     * @param o member前的参数
     * @return 条件
     */
    public Condition reverseMember(Object o) {
        return new Condition(this, ConditionTypes.reverseMember, new Object[]{o});
    }

    /**
     * 将参数本身置于not member之后
     *
     * @param o not member前的参数
     * @return 条件
     */
    public Condition reverseNotMember(Object o) {
        return new Condition(this, ConditionTypes.reverseNotMember, new Object[]{o});
    }
}
