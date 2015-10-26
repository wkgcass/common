package net.cassite.datafacade;

/**
 * 可以放在Where子句中作为条件的对象
 */
public interface Where {

    boolean isAnd();

    boolean isOr();

    boolean isCondition();

    boolean isExpression();
}
