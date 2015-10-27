package net.cassite.daf4j;

import java.util.Arrays;

/**
 * 表示一个普通的表达式(表达式返回值不能进行AndOr操作,也不能进行比较操作)
 */
public class Expression extends Parameter implements Where, IExpression {
    final ExpressionTypes type;
    final Object[] parameters;

    Expression(ExpressionTypes type, Object... parameters) {
        this.type = type;
        this.parameters = Arrays.copyOf(parameters, parameters.length);
    }

    @Override
    public String toString() {
        return DataUtils.expToStringUtil(type, parameters);
    }

    @Override
    public boolean isAnd() {
        return false;
    }

    @Override
    public boolean isOr() {
        return false;
    }

    @Override
    public ExpressionTypes expType() {
        return type;
    }

    @Override
    public Object[] expArgs() {
        return parameters;
    }

    @Override
    public boolean isCondition() {
        return false;
    }

    @Override
    public boolean isExpression() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return DataUtils.expressionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return DataUtils.expressionHashCode(this);
    }
}
