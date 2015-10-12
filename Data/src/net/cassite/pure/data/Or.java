package net.cassite.pure.data;

import java.util.ArrayList;
import java.util.List;

public class Or implements Where, AndOr {
    final List<Condition> conditions = new ArrayList<Condition>();
    final List<And> ands = new ArrayList<And>();
    final List<ExpressionBoolean> expBools = new ArrayList<ExpressionBoolean>();

    @Override
    public And and(ExpressionBoolean expBool) {
        And a = new And();
        a.ors.add(this);
        a.expBools.add(expBool);
        return a;
    }

    @Override
    public Or or(ExpressionBoolean expBool) {
        expBools.add(expBool);
        return this;
    }

    public Or or(Condition condition) {
        conditions.add(condition);
        return this;
    }

    public Or or(Or o) {
        conditions.addAll(o.conditions);
        ands.addAll(o.ands);
        return this;
    }

    public Or or(And a) {
        ands.add(a);
        return this;
    }

    public And and(Condition condition) {
        And a = new And();
        a.conditions.add(condition);
        a.ors.add(this);
        return a;
    }

    public And and(And a) {
        a.ors.add(this);
        return a;
    }

    public And and(Or o) {
        And a = new And();
        a.ors.add(this);
        a.ors.add(o);
        return a;
    }

    @Override
    public List<And> getAndList() {
        return ands;
    }

    @Override
    public List<Or> getOrList() {
        return null;
    }

    @Override
    public List<Condition> getConditionList() {
        return conditions;
    }

    @Override
    public boolean isAnd() {
        return false;
    }

    @Override
    public boolean isOr() {
        return true;
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
                sb.append(" or ");
            }
            sb.append(c.toString());
        }
        for (And a : ands) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(" or ");
            }
            sb.append("(").append(a.toString()).append(")");
        }
        for (ExpressionBoolean expBool : expBools) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(" or ");
            }
            sb.append(expBool);
        }
        return sb.toString();
    }
}
