package net.cassite.pure.data;

/**
 * 
 * @param <T>
 */
public abstract class ParameterComparable<T extends Comparable<T>> extends Parameter implements Comparable<T> {
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
        return new Condition(this, ConditionTypes.between, new Object[]{this, start, end});
    }

    public ExpressionComparable add(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.add, this, obj);
    }

    public ExpressionComparable add(Number obj) {
        return new ExpressionComparable(ExpressionType.add, this, obj);
    }

    public ExpressionComparable minus(Number obj) {
        return new ExpressionComparable(ExpressionType.minus, this, obj);
    }

    public ExpressionComparable minus(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.minus, this, obj);
    }

    public ExpressionComparable multi(Number obj) {
        return new ExpressionComparable(ExpressionType.multi, this, obj);
    }

    public ExpressionComparable multi(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.multi, this, obj);
    }

    public ExpressionComparable divide(Number obj) {
        return new ExpressionComparable(ExpressionType.divide, this, obj);
    }

    public ExpressionComparable divide(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.divide, this, obj);
    }

    public ExpressionComparable mod(Number obj) {
        return new ExpressionComparable(ExpressionType.mod, this, obj);
    }

    public ExpressionComparable mod(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.mod, this, obj);
    }

    public ExpressionComparable reverseMinus(Number obj) {
        return new ExpressionComparable(ExpressionType.minus, obj, this);
    }

    public ExpressionComparable reverseMinus(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.minus, obj, this);
    }

    public ExpressionComparable reverseDivide(Number obj) {
        return new ExpressionComparable(ExpressionType.divide, obj, this);
    }

    public ExpressionComparable reverseDivide(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.divide, obj, this);
    }

    public ExpressionComparable reverseMod(Number obj) {
        return new ExpressionComparable(ExpressionType.mod, obj, this);
    }

    public ExpressionComparable reverseMod(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.mod, obj, this);
    }

    public ExpressionComparable unary_negative() {
        return new ExpressionComparable(ExpressionType.unary_negative, this);
    }
}
