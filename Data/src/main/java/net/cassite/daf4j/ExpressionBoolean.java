package net.cassite.daf4j;

import java.util.Arrays;

/**
 * 表示一个返回布尔值的表达式(可以进行AndOr操作,但不能进行比较操作)
 */
public class ExpressionBoolean extends Parameter implements Where, AndOr, IExpression {

    final ExpressionTypes type;
    final Object[] parameters;

    ExpressionBoolean(ExpressionTypes type, Object... parameters) {
        this.type = type;
        this.parameters = Arrays.copyOf(parameters, parameters.length);
    }

    @Override
    public And and(ExpressionBoolean expBool) {
        And a = new And();
        a.expBools.add(expBool);
        a.expBools.add(this);
        return a;
    }

    @Override
    public Or or(ExpressionBoolean expBool) {
        Or o = new Or();
        o.expBools.add(this);
        o.expBools.add(expBool);
        return o;
    }

    @Override
    public And and(Condition condition) {
        And a = new And();
        a.conditions.add(condition);
        a.expBools.add(this);
        return a;
    }

    @Override
    public And and(And a) {
        a.expBools.add(this);
        return a;
    }

    @Override
    public And and(Or or) {
        And a = new And();
        a.ors.add(or);
        a.expBools.add(this);
        return a;
    }

    @Override
    public Or or(Condition condition) {
        Or o = new Or();
        o.conditions.add(condition);
        o.expBools.add(this);
        return o;
    }

    @Override
    public Or or(And a) {
        Or o = new Or();
        o.ands.add(a);
        o.expBools.add(this);
        return o;
    }

    @Override
    public Or or(Or o) {
        o.expBools.add(this);
        return o;
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
