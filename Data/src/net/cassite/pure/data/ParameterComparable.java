package net.cassite.pure.data;

/**
 * Created by wkgcass on 15/10/11.
 */
public class ParameterComparable extends Parameter {
    public Condition $gt(Comparable<?> obj) {
        return new Condition(this, ConditionTypes.gt, new Object[]{obj});
    }

    public Condition $lt(Comparable<?> obj) {
        return new Condition(this, ConditionTypes.lt, new Object[]{obj});
    }

    public Condition $ge(Comparable<?> obj) {
        return new Condition(this, ConditionTypes.ge, new Object[]{obj});
    }

    public Condition $le(Comparable<?> obj) {
        return new Condition(this, ConditionTypes.le, new Object[]{obj});
    }

    public <T> Condition between(Comparable<T> start, Comparable<T> end) {
        return new Condition(this, ConditionTypes.between, new Object[]{start, end});
    }

    public Expression add(Comparable<? extends Number> obj) {
        return new Expression(ExpressionType.add, obj);
    }

    public Expression add(Number obj) {
        return new Expression(ExpressionType.add, obj);
    }

    public Expression minus(Number obj) {
        return new Expression(ExpressionType.minus, obj);
    }

    public Expression minus(Comparable<? extends Number> obj) {
        return new Expression(ExpressionType.minus, obj);
    }

    public Expression multi(Number obj) {
        return new Expression(ExpressionType.multi, obj);
    }

    public Expression multi(Comparable<? extends Number> obj) {
        return new Expression(ExpressionType.multi, obj);
    }

    public Expression divide(Number obj) {
        return new Expression(ExpressionType.divide, obj);
    }

    public Expression divide(Comparable<? extends Number> obj) {
        return new Expression(ExpressionType.divide, obj);
    }

    public Expression mod(Number obj) {
        return new Expression(ExpressionType.mod, obj);
    }

    public Expression mod(Comparable<? extends Number> obj) {
        return new Expression(ExpressionType.mod, obj);
    }

    public Expression reverseMinus(Number obj) {
        return new Expression(ExpressionType.reverseMinus, obj);
    }

    public Expression reverseMinus(Comparable<? extends Number> obj) {
        return new Expression(ExpressionType.reverseMinus, obj);
    }

    public Expression reverseDivide(Number obj) {
        return new Expression(ExpressionType.reverseDivide, obj);
    }

    public Expression reverseDivide(Comparable<? extends Number> obj) {
        return new Expression(ExpressionType.reverseDivide, obj);
    }

    public Expression reverseMod(Number obj) {
        return new Expression(ExpressionType.reverseMod, obj);
    }

    public Expression reverseMod(Comparable<? extends Number> obj) {
        return new Expression(ExpressionType.reverseMod, obj);
    }
}
