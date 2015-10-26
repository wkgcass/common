package net.cassite.datafacade;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询条件中的与操作<br>
 * 该对象中存储的对象应当以"与"逻辑操作连接起来
 */
public class And implements Where, AndOr {
    final List<Condition> conditions = new ArrayList<Condition>();
    final List<Or> ors = new ArrayList<Or>();
    final List<ExpressionBoolean> expBools = new ArrayList<ExpressionBoolean>();

    @Override
    public And and(Condition condition) {
        conditions.add(condition);
        return this;
    }

    @Override
    public And and(And a) {
        this.conditions.addAll(a.conditions);
        this.ors.addAll(a.ors);
        return this;
    }

    @Override
    public And and(Or o) {
        ors.add(o);
        return this;
    }

    @Override
    public And and(ExpressionBoolean expBool) {
        expBool.and(expBool);
        return this;
    }

    @Override
    public Or or(Condition condition) {
        Or o = new Or();
        o.conditions.add(condition);
        o.ands.add(this);
        return o;
    }

    @Override
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

    @Override
    public Or or(And a) {
        Or o = new Or();
        o.ands.add(this);
        o.ands.add(a);
        return o;
    }

    /**
     * 获取存储的"或"逻辑操作对象
     *
     * @return 存放Or的List
     */
    public List<Or> getOrList() {
        return ors;
    }

    /**
     * 获取存储的"条件"对象
     *
     * @return 存放Condition的List
     */
    public List<Condition> getConditionList() {
        return conditions;
    }

    /**
     * 获取存储的"返回布尔值的表达式"对象
     *
     * @return 存放ExpressionBoolean的List
     */
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
