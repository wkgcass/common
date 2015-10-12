package net.cassite.pure.data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by blador01 on 2015/10/12.
 */
public class ExpressionComparable extends ParameterComparable implements Where {
    final ExpressionType type;
    final Object[] parameters;

    ExpressionComparable(ExpressionType type, Object... parameters) {
        this.type = type;
        this.parameters = Arrays.copyOf(parameters, parameters.length);
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
