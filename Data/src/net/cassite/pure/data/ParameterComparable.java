package net.cassite.pure.data;

/**
 * Created by wkgcass on 15/10/11.
 */
public abstract class ParameterComparable extends Parameter {
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

    public ExpressionComparable add(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.add, obj);
    }

    public ExpressionComparable add(Number obj) {
        return new ExpressionComparable(ExpressionType.add, obj);
    }

    public ExpressionComparable minus(Number obj) {
        return new ExpressionComparable(ExpressionType.minus, obj);
    }

    public ExpressionComparable minus(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.minus, obj);
    }

    public ExpressionComparable multi(Number obj) {
        return new ExpressionComparable(ExpressionType.multi, obj);
    }

    public ExpressionComparable multi(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.multi, obj);
    }

    public ExpressionComparable divide(Number obj) {
        return new ExpressionComparable(ExpressionType.divide, obj);
    }

    public ExpressionComparable divide(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.divide, obj);
    }

    public ExpressionComparable mod(Number obj) {
        return new ExpressionComparable(ExpressionType.mod, obj);
    }

    public ExpressionComparable mod(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.mod, obj);
    }

    public ExpressionComparable reverseMinus(Number obj) {
        return new ExpressionComparable(ExpressionType.reverseMinus, obj);
    }

    public ExpressionComparable reverseMinus(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.reverseMinus, obj);
    }

    public ExpressionComparable reverseDivide(Number obj) {
        return new ExpressionComparable(ExpressionType.reverseDivide, obj);
    }

    public ExpressionComparable reverseDivide(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.reverseDivide, obj);
    }

    public ExpressionComparable reverseMod(Number obj) {
        return new ExpressionComparable(ExpressionType.reverseMod, obj);
    }

    public ExpressionComparable reverseMod(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.reverseMod, obj);
    }

    public ExpressionComparable unary_negative() {
        return new ExpressionComparable(ExpressionType.unary_negative);
    }
}
