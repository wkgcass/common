package net.cassite.daf4j;

import java.util.Arrays;

/**
 * 表示一个返回可比较值的表达式(可以进行比较,但不能进行AndOr操作)
 */
public class ExpressionComparable extends ParameterComparable<ExpressionComparable> implements Where, IExpression {
    final ExpressionTypes type;
    final Object[] parameters;

    ExpressionComparable(ExpressionTypes type, Object... parameters) {
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
    public int compareTo(ExpressionComparable o) {
        return 0;
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
