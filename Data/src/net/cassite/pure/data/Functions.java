package net.cassite.pure.data;

public class Functions {
    private Functions() {
    }

    public static ExpressionComparable sum(DataComparable<? extends Number> toSum) {
        return new ExpressionComparable(ExpressionType.sum, toSum);
    }

    public static ExpressionComparable avg(DataComparable<? extends Number> toAvg) {
        return new ExpressionComparable(ExpressionType.avg, toAvg);
    }

    public static ExpressionComparable count(IData<?> toCount) {
        return new ExpressionComparable(ExpressionType.count, toCount);
    }

    public static ExpressionComparable max(DataComparable<? extends Comparable<?>> col) {
        return new ExpressionComparable(ExpressionType.max, col);
    }

    public static ExpressionComparable min(DataComparable<? extends Comparable<?>> col) {
        return new ExpressionComparable(ExpressionType.min, col);
    }

    public static ExpressionBoolean exists(PreResult<?> subQuery) {
        return new ExpressionBoolean(ExpressionType.exists, subQuery);
    }

    public static ExpressionBoolean notExists(PreResult<?> subQuery) {
        return new ExpressionBoolean(ExpressionType.notExists, subQuery);
    }

    public static Expression concat(IData<String> data, String str) {
        return new Expression(ExpressionType.concat, data, str);
    }

    public static Expression concat(String str, IData<String> data) {
        return new Expression(ExpressionType.concat, str, data);
    }

    public static Expression concat(IData<String> data1, IData<String> data2) {
        return new Expression(ExpressionType.concat, data1, data2);
    }

    public static Expression substring(IData<String> data, int startAt, int endAt) {
        return new Expression(ExpressionType.substring, startAt, endAt);
    }

    public static Expression trim(IData<String> data) {
        return new Expression(ExpressionType.trim, data);
    }

    public static Expression lower(IData<String> data) {
        return new Expression(ExpressionType.lower, data);
    }

    public static Expression upper(IData<String> data) {
        return new Expression(ExpressionType.upper, data);
    }

    public static ExpressionComparable length(IData<String> data) {
        return new ExpressionComparable(ExpressionType.length, data);
    }

    public static Expression locate(String toLocate, IData<String> sample) {
        return new Expression(ExpressionType.trim, toLocate, sample);
    }

    public static Expression locate(IData<String> toLocate, String sample) {
        return new Expression(ExpressionType.trim, toLocate, sample);
    }

    public static Expression locate(IData<String> toLocate, IData<String> sample) {
        return new Expression(ExpressionType.trim, toLocate, sample);
    }
}
