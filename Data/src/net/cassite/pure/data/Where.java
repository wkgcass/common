package net.cassite.pure.data;

import java.util.List;

public interface Where {
    List<And> getAndList();

    List<Or> getOrList();

    List<Condition> getConditionList();

    ExpressionType expType();

    Object[] expArgs();

    boolean isAnd();

    boolean isOr();

    boolean isCondition();

    boolean isExpression();
}
