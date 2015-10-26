package net.cassite.datafacade;

/**
 * 用于创建函数<br>
 * 建议使用import static直接使用其下方法
 */
public class Functions {
        private Functions() {
        }

        /**
         * 求和
         *
         * @param toSum 要求和的字段
         * @return 可进行比较的表达式
         */
        public static ExpressionComparable sum(DataComparable<? extends Number> toSum) {
                return new ExpressionComparable(ExpressionTypes.sum, toSum);
        }

        /**
         * 平均数
         *
         * @param toAvg 要求平均的字段
         * @return 可进行比较的表达式
         */
        public static ExpressionComparable avg(DataComparable<? extends Number> toAvg) {
                return new ExpressionComparable(ExpressionTypes.avg, toAvg);
        }

        /**
         * 计数
         *
         * @param toCount 要计数的字段
         * @return 可进行比较的表达式
         */
        public static ExpressionComparable count(IData<?> toCount) {
                return new ExpressionComparable(ExpressionTypes.count, toCount);
        }

        /**
         * 最大值
         *
         * @param col 要进行比较的字段
         * @return 可进行比较的表达式
         */
        public static ExpressionComparable max(DataComparable<? extends Comparable<?>> col) {
                return new ExpressionComparable(ExpressionTypes.max, col);
        }

        /**
         * 最小值
         *
         * @param col 要进行比较的字段
         * @return 可进行比较的表达式
         */
        public static ExpressionComparable min(DataComparable<? extends Comparable<?>> col) {
                return new ExpressionComparable(ExpressionTypes.min, col);
        }

        /**
         * 子查询有结果
         *
         * @param subQuery 子查询
         * @return 可进行逻辑操作的表达式
         */
        public static ExpressionBoolean exists(PreResult<?> subQuery) {
                return new ExpressionBoolean(ExpressionTypes.exists, subQuery);
        }

        /**
         * 子查询无结果
         *
         * @param subQuery 子查询
         * @return 可进行逻辑操作的表达式
         */
        public static ExpressionBoolean notExists(PreResult<?> subQuery) {
                return new ExpressionBoolean(ExpressionTypes.notExists, subQuery);
        }

        /**
         * 字符串拼接(字段在前)
         *
         * @param data 要拼接的字段
         * @param str  要拼接的文字(常量)
         * @return 普通表达式
         */
        public static Expression concat(IData<String> data, String str) {
                return new Expression(ExpressionTypes.concat, data, str);
        }

        /**
         * 字符串拼接(字段在后)
         *
         * @param str  要拼接的文字(常量)
         * @param data 要拼接的字段
         * @return 普通表达式
         */
        public static Expression concat(String str, IData<String> data) {
                return new Expression(ExpressionTypes.concat, str, data);
        }

        /**
         * 字符串拼接(两个都是字段)
         *
         * @param data1 要拼接的字段1
         * @param data2 要拼接的字段2
         * @return 普通表达式
         */
        public static Expression concat(IData<String> data1, IData<String> data2) {
                return new Expression(ExpressionTypes.concat, data1, data2);
        }

        /**
         * 子字符串
         *
         * @param data    在该字段中取子字符串
         * @param startAt 起始位置
         * @param endAt   结束位置
         * @return 普通表达式
         */
        public static Expression substring(IData<String> data, int startAt, int endAt) {
                return new Expression(ExpressionTypes.substring, data, startAt, endAt);
        }

        /**
         * 去除字符串前后空白
         *
         * @param data 要去空的字段
         * @return 普通表达式
         */
        public static Expression trim(IData<String> data) {
                return new Expression(ExpressionTypes.trim, data);
        }

        /**
         * 字符串转为小写
         *
         * @param data 要转换的字段
         * @return 普通表达式
         */
        public static Expression lower(IData<String> data) {
                return new Expression(ExpressionTypes.lower, data);
        }

        /**
         * 字符串转为大写
         *
         * @param data 要转换的字段
         * @return 普通表达式
         */
        public static Expression upper(IData<String> data) {
                return new Expression(ExpressionTypes.upper, data);
        }

        /**
         * 字符串长度
         *
         * @param data 要去长度的字段
         * @return 可进行比较的表达式
         */
        public static ExpressionComparable length(IData<String> data) {
                return new ExpressionComparable(ExpressionTypes.length, data);
        }

        /**
         * 定位子串(常量)在主串中位置
         *
         * @param toLocate 子串(常量)
         * @param sample   主串(字段)
         * @return 可进行比较的表达式
         */
        public static ExpressionComparable locate(String toLocate, IData<String> sample) {
                return new ExpressionComparable(ExpressionTypes.locate, toLocate, sample);
        }

        /**
         * 定位子串(字段)在主串中位置
         *
         * @param toLocate 子串(字段)
         * @param sample   主串(常量)
         * @return 可进行比较的表达式
         */
        public static ExpressionComparable locate(IData<String> toLocate, String sample) {
                return new ExpressionComparable(ExpressionTypes.locate, toLocate, sample);
        }

        /**
         * 定位子串(字段)在主串(字段)中位置
         *
         * @param toLocate 字串(字段)
         * @param sample   主串(字段)
         * @return 可进行比较的表达式
         */
        public static ExpressionComparable locate(IData<String> toLocate, IData<String> sample) {
                return new ExpressionComparable(ExpressionTypes.locate, toLocate, sample);
        }
}
