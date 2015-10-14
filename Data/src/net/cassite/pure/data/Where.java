package net.cassite.pure.data;

import java.util.List;

public interface Where {

    boolean isAnd();

    boolean isOr();

    boolean isCondition();

    boolean isExpression();
}
