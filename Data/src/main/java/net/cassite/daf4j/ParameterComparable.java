package net.cassite.daf4j;

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
         * @param <TT>  元素类型
         * @return 条件
         */
        public <TT> Condition between(Comparable<TT> start, Comparable<TT> end) {
                return new Condition(this, ConditionTypes.between, new Object[]{start, end});
        }

        /**
         * +运算
         *
         * @param obj 要进行运算的对象
         * @return 可进行比较的表达式
         */
        public ExpressionComparable add(Comparable<? extends Number> obj) {
                return new ExpressionComparable(ExpressionTypes.add, this, obj);
        }

        /**
         * -运算
         *
         * @param obj 要进行运算的对象
         * @return 可进行比较的表达式
         */
        public ExpressionComparable minus(Comparable<? extends Number> obj) {
                return new ExpressionComparable(ExpressionTypes.minus, this, obj);
        }

        /**
         * *运算
         *
         * @param obj 要进行运算的对象
         * @return 可进行比较的表达式
         */
        public ExpressionComparable multi(Comparable<? extends Number> obj) {
                return new ExpressionComparable(ExpressionTypes.multi, this, obj);
        }

        /**
         * /运算
         *
         * @param obj 要进行运算的对象
         * @return 可进行比较的表达式
         */
        public ExpressionComparable divide(Comparable<? extends Number> obj) {
                return new ExpressionComparable(ExpressionTypes.divide, this, obj);
        }

        /**
         * %运算
         *
         * @param obj 要进行运算的对象
         * @return 可进行比较的表达式
         */
        public ExpressionComparable mod(Comparable<? extends Number> obj) {
                return new ExpressionComparable(ExpressionTypes.mod, this, obj);
        }

        /**
         * -运算(该参数放在后面)
         *
         * @param obj 要进行运算的对象(放在前面)
         * @return 可进行比较的表达式
         */
        public ExpressionComparable reverseMinus(Comparable<? extends Number> obj) {
                return new ExpressionComparable(ExpressionTypes.minus, obj, this);
        }

        /**
         * /运算(该参数放在后面)
         *
         * @param obj 要进行运算的对象(放在前面)
         * @return 可进行比较的表达式
         */
        public ExpressionComparable reverseDivide(Comparable<? extends Number> obj) {
                return new ExpressionComparable(ExpressionTypes.divide, obj, this);
        }

        /**
         * %运算(该参数放在后面)
         *
         * @param obj 要进行运算的对象(放在前面)
         * @return 可进行比较的表达式
         */
        public ExpressionComparable reverseMod(Comparable<? extends Number> obj) {
                return new ExpressionComparable(ExpressionTypes.mod, obj, this);
        }

        /**
         * 取负数
         *
         * @return 可进行比较的表达式
         */
        public ExpressionComparable unary_negative() {
                return new ExpressionComparable(ExpressionTypes.unary_negative, this);
        }
}
