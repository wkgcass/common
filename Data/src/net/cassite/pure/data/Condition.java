package net.cassite.pure.data;

import java.util.ArrayList;
import java.util.List;

public class Condition implements Where, AndOr {

    public final Parameter data;
    public final ConditionTypes type;
    public final List<Object> args;

    Condition(Parameter data, ConditionTypes type, Object[] args) {
        this.data = data;
        this.type = type;
        this.args = new ArrayList<Object>(args.length);
        for (Object o : args) {
            this.args.add(o);
        }
    }

    public And and(Condition condition) {
        And a = new And();
        a.conditions.add(this);
        a.conditions.add(condition);
        return a;
    }

    public And and(And a) {
        a.conditions.add(this);
        return a;
    }

    public And and(Or or) {
        And a = new And();
        a.conditions.add(this);
        a.ors.add(or);
        return a;
    }

    public Or or(Condition condition) {
        Or o = new Or();
        o.conditions.add(this);
        o.conditions.add(condition);
        return o;
    }

    public Or or(And a) {
        Or o = new Or();
        o.conditions.add(this);
        o.ands.add(a);
        return o;
    }

    public Or or(Or o) {
        o.conditions.add(this);
        return o;
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
        List<Condition> list = new ArrayList<Condition>(1);
        list.add(this);
        return list;
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
        return null;
    }

    @Override
    public Object[] expArgs() {
        return null;
    }

    @Override
    public boolean isCondition() {
        return true;
    }

    @Override
    public boolean isExpression() {
        return false;
    }

    @Override
    public String toString() {
        return data + "." + type + args;
    }
}
