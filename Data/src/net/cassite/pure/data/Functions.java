package net.cassite.pure.data;

import java.util.Comparator;

public class Functions {
    private Functions() {
    }

    public static Expression sum(DataComparable<? extends Number> toSum) {
        return new Expression(ExpressionType.sum, toSum);
    }

    public static Expression avg(DataComparable<? extends Number> toAvg) {
        return new Expression(ExpressionType.avg, toAvg);
    }

    public static Expression count(Data<?> toCount) {
        return new Expression(ExpressionType.count, toCount);
    }

    public static Expression max(DataComparable<? extends Comparable<?>> col) {
        return new Expression(ExpressionType.max, col);
    }

    public static Expression min(DataComparable<? extends Comparable<?>> col) {
        return new Expression(ExpressionType.min, col);
    }

    public static Expression exists(PreResult<?> subQuery) {
        return new Expression(ExpressionType.exists, subQuery);
    }

    public static Expression notExists(PreResult<?> subQuery) {
        return new Expression(ExpressionType.notExists, subQuery);
    }
}
