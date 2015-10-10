package net.cassite.pure.data;

public class Parameter {
        public Condition $gt(Object obj) {
                return new Condition(this, ConditionTypes.gt, new Object[] { obj });
        }

        public Condition $lt(Object obj) {
                return new Condition(this, ConditionTypes.lt, new Object[] { obj });
        }

        public Condition $eq(Object obj) {
                return new Condition(this, ConditionTypes.eq, new Object[] { obj });
        }

        public Condition $ge(Object obj) {
                return new Condition(this, ConditionTypes.ge, new Object[] { obj });
        }

        public Condition $le(Object obj) {
                return new Condition(this, ConditionTypes.le, new Object[] { obj });
        }

        public Condition $ne(Object obj) {
                return new Condition(this, ConditionTypes.ne, new Object[] { obj });
        }

        public Condition between(Object start, Object end) {
                return new Condition(this, ConditionTypes.between, new Object[] { start, end });
        }

        public Condition in(Object... objs) {
                return new Condition(this, ConditionTypes.in, objs);
        }

        public Condition notIn(Object... objs) {
                return new Condition(this, ConditionTypes.notIn, objs);
        }

        public Condition like(Object... obj) {
                return new Condition(this, ConditionTypes.like, new Object[] { obj });
        }

        public Condition isNull() {
                return new Condition(this, ConditionTypes.isNull, new Object[0]);
        }

        public Expression add(Object obj) {
                return new Expression(ExpressionType.add, obj);
        }

        public Expression minus(Object obj) {
                return new Expression(ExpressionType.minus, obj);
        }

        public Expression multi(Object obj) {
                return new Expression(ExpressionType.multi, obj);
        }

        public Expression divide(Object obj) {
                return new Expression(ExpressionType.divide, obj);
        }

        public Expression mod(Object obj) {
                return new Expression(ExpressionType.mod, obj);
        }

        public Expression reverseMinus(Object obj) {
                return new Expression(ExpressionType.reverseMinus, obj);
        }

        public Expression reverseDivide(Object obj) {
                return new Expression(ExpressionType.reverseDivide, obj);
        }

        public Expression reverseMod(Object obj) {
                return new Expression(ExpressionType.reverseMod, obj);
        }
}
