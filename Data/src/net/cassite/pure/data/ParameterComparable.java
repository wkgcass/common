package net.cassite.pure.data;

/**
 * 可进行比较的参数
 *
 * @param <T> 类型
 */
public abstract class ParameterComparable<T extends Comparable<T>> extends Parameter implements Comparable<T> {
    /**
     * 大于
     *
     * @param obj 要判断大小的对象
     * @return 条件
     */
    public Condition $gt(Comparable<?> obj) {
        return new Condition(this, ConditionTypes.gt, new Object[]{obj});
    }

    /**
     * 小于
     *
     * @param obj 要判断大小的对象
     * @return 条件
     */
    public Condition $lt(Comparable<?> obj) {
        return new Condition(this, ConditionTypes.lt, new Object[]{obj});
    }

    /**
     * 大于等于
     *
     * @param obj 要判断大小的对象
     * @return 条件
     */
    public Condition $ge(Comparable<?> obj) {
        return new Condition(this, ConditionTypes.ge, new Object[]{obj});
    }

    /**
     * 小于等于
     *
     * @param obj 要判断大小的对象
     * @return 条件
     */
    public Condition $le(Comparable<?> obj) {
        return new Condition(this, ConditionTypes.le, new Object[]{obj});
    }

    /**
     * 在起始和结束之间
     *
     * @param start 起始
     * @param end   结束
     * @param <T>   元素类型
     * @return 条件
     */
    public <T> Condition between(Comparable<T> start, Comparable<T> end) {
        return new Condition(this, ConditionTypes.between, new Object[]{this, start, end});
    }

    /**
     * +运算
     *
     * @param obj 要进行运算的对象
     * @return 可进行比较的表达式
     */
    public ExpressionComparable add(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.add, this, obj);
    }

    /**
     * +运算
     *
     * @param obj 要进行运算的对象
     * @return 可进行比较的表达式
     */
    public ExpressionComparable add(Number obj) {
        return new ExpressionComparable(ExpressionType.add, this, obj);
    }

    /**
     * -运算
     *
     * @param obj 要进行运算的对象
     * @return 可进行比较的表达式
     */
    public ExpressionComparable minus(Number obj) {
        return new ExpressionComparable(ExpressionType.minus, this, obj);
    }

    /**
     * -运算
     *
     * @param obj 要进行运算的对象
     * @return 可进行比较的表达式
     */
    public ExpressionComparable minus(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.minus, this, obj);
    }

    /**
     * *运算
     *
     * @param obj 要进行运算的对象
     * @return 可进行比较的表达式
     */
    public ExpressionComparable multi(Number obj) {
        return new ExpressionComparable(ExpressionType.multi, this, obj);
    }

    /**
     * *运算
     *
     * @param obj 要进行运算的对象
     * @return 可进行比较的表达式
     */
    public ExpressionComparable multi(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.multi, this, obj);
    }

    /**
     * /运算
     *
     * @param obj 要进行运算的对象
     * @return 可进行比较的表达式
     */
    public ExpressionComparable divide(Number obj) {
        return new ExpressionComparable(ExpressionType.divide, this, obj);
    }

    /**
     * /运算
     *
     * @param obj 要进行运算的对象
     * @return 可进行比较的表达式
     */
    public ExpressionComparable divide(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.divide, this, obj);
    }

    /**
     * %运算
     *
     * @param obj 要进行运算的对象
     * @return 可进行比较的表达式
     */
    public ExpressionComparable mod(Number obj) {
        return new ExpressionComparable(ExpressionType.mod, this, obj);
    }

    /**
     * %运算
     *
     * @param obj 要进行运算的对象
     * @return 可进行比较的表达式
     */
    public ExpressionComparable mod(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.mod, this, obj);
    }

    /**
     * -运算(该参数放在后面)
     *
     * @param obj 要进行运算的对象(放在前面)
     * @return 可进行比较的表达式
     */
    public ExpressionComparable reverseMinus(Number obj) {
        return new ExpressionComparable(ExpressionType.minus, obj, this);
    }

    /**
     * -运算(该参数放在后面)
     *
     * @param obj 要进行运算的对象(放在前面)
     * @return 可进行比较的表达式
     */
    public ExpressionComparable reverseMinus(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.minus, obj, this);
    }

    /**
     * /运算(该参数放在后面)
     *
     * @param obj 要进行运算的对象(放在前面)
     * @return 可进行比较的表达式
     */
    public ExpressionComparable reverseDivide(Number obj) {
        return new ExpressionComparable(ExpressionType.divide, obj, this);
    }

    /**
     * /运算(该参数放在后面)
     *
     * @param obj 要进行运算的对象(放在前面)
     * @return 可进行比较的表达式
     */
    public ExpressionComparable reverseDivide(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.divide, obj, this);
    }

    /**
     * %运算(该参数放在后面)
     *
     * @param obj 要进行运算的对象(放在前面)
     * @return 可进行比较的表达式
     */
    public ExpressionComparable reverseMod(Number obj) {
        return new ExpressionComparable(ExpressionType.mod, obj, this);
    }

    /**
     * %运算(该参数放在后面)
     *
     * @param obj 要进行运算的对象(放在前面)
     * @return 可进行比较的表达式
     */
    public ExpressionComparable reverseMod(Comparable<? extends Number> obj) {
        return new ExpressionComparable(ExpressionType.mod, obj, this);
    }

    /**
     * 取负数
     *
     * @return 可进行比较的表达式
     */
    public ExpressionComparable unary_negative() {
        return new ExpressionComparable(ExpressionType.unary_negative, this);
    }
}
