package net.cassite.pure.data;

public class Functions {
        private Functions() {
        }

        public static Expression sum(Data<?> toSum) {
                return new Expression(ExpressionType.sum, toSum);
        }

        public static Expression avg(Data<?> toAvg) {
                return new Expression(ExpressionType.avg, toAvg);
        }

        public static Expression count(Data<?> toCount) {
                return new Expression(ExpressionType.count, toCount);
        }

        public static Expression max(Data<?> col) {
                return new Expression(ExpressionType.max, col);
        }

        public static Expression min(Data<?> col) {
                return new Expression(ExpressionType.min, col);
        }
}
