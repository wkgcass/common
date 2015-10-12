package net.cassite.pure.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by blador01 on 2015/10/12.
 */
public class ExpressionBoolean extends Parameter implements Where, AndOr {

    final ExpressionType type;
    final Object[] parameters;

    ExpressionBoolean(ExpressionType type, Object... parameters) {
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
    public List<And> getAndList() {
        return null;
    }

    @Override
    public List<Or> getOrList() {
        return null;
    }

    @Override
    public List<Condition> getConditionList() {
        return null;
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
    public ExpressionType expType() {
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
}
