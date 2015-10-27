package net.cassite.daf4j;

/**
 * 条件表达式类型,在Parameter和其子类中调用
 *
 * @see Parameter
 * @see ParameterComparable
 * @see ParameterAggregate
 */
public enum ConditionTypes {
    gt, lt, eq, ge, le, ne, between, in, notIn, like, isNull, isNotNull, member, reverseMember, notMember, reverseNotMember, EXTENSION
}
