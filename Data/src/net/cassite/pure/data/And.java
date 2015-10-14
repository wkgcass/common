package net.cassite.pure.data;

import java.util.ArrayList;
import java.util.List;

public class And implements Where, AndOr {
    final List<Condition> conditions = new ArrayList<Condition>();
    final List<Or> ors = new ArrayList<Or>();
    final List<ExpressionBoolean> expBools = new ArrayList<ExpressionBoolean>();

    public And and(Condition condition) {
        conditions.add(condition);
        return this;
    }

    public And and(And a) {
        this.conditions.addAll(a.conditions);
        this.ors.addAll(a.ors);
        return this;
    }

    public And and(Or o) {
        ors.add(o);
        return this;
    }

    @Override
    public And and(ExpressionBoolean expBool) {
        expBool.and(expBool);
        return this;
    }

    public Or or(Condition condition) {
        Or o = new Or();
        o.conditions.add(condition);
        o.ands.add(this);
        return o;
    }

    public Or or(Or o) {
        o.ands.add(this);
        return o;
    }

    @Override
    public Or or(ExpressionBoolean expBool) {
        Or o = new Or();
        o.ands.add(this);
        o.expBools.add(expBool);
        return o;
    }

    public Or or(And a) {
        Or o = new Or();
        o.ands.add(this);
        o.ands.add(a);
        return o;
    }

    public List<Or> getOrList() {
        return ors;
    }

    public List<Condition> getConditionList() {
        return conditions;
    }

    public List<ExpressionBoolean> getExpBoolList() {
        return expBools;
    }

    @Override
    public boolean isAnd() {
        return true;
    }

    @Override
    public boolean isOr() {
        return false;
    }

    @Override
    public boolean isCondition() {
        return false;
    }

    @Override
    public boolean isExpression() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (Condition c : conditions) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(" and ");
            }
            sb.append(c.toString());
        }
        for (Or o : ors) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(" and ");
            }
            sb.append("(").append(o.toString()).append(")");
        }
        for (ExpressionBoolean expBool : expBools) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(" and ");
            }
            sb.append(expBool);
        }
        return sb.toString();
    }
}
