package net.cassite.daf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 条件表达式.仅在Parameter和其子类中创建.可进行的操作可查看ConditionTypes以及Parameter及其子类.
 *
 * @see ConditionTypes
 * @see Parameter
 * @see ParameterComparable
 * @see ParameterAggregate
 */
public class Condition implements Where, AndOr {

        /**
         * Parameter对象
         *
         * @see Parameter
         */
        public final Parameter data;
        /**
         * 条件类型
         *
         * @see ConditionTypes
         */
        public final ConditionTypes type;
        /**
         * 该表达式参数
         */
        public final List<Object> args;

        Condition(Parameter data, ConditionTypes type, Object[] args) {
                this.data = data;
                this.type = type;
                this.args = new ArrayList<Object>(args.length);
                Collections.addAll(this.args, args);
        }

        public And and(Condition condition) {
                And a = new And();
                a.conditions.add(this);
                a.conditions.add(condition);
                return a;
        }

        public And and(And a) {
                a.conditions.add(this);
                return a;
        }

        public And and(Or or) {
                And a = new And();
                a.conditions.add(this);
                a.ors.add(or);
                return a;
        }

        @Override
        public And and(ExpressionBoolean expBool) {
                And a = new And();
                a.conditions.add(this);
                a.expBools.add(expBool);
                return a;
        }

        public Or or(Condition condition) {
                Or o = new Or();
                o.conditions.add(this);
                o.conditions.add(condition);
                return o;
        }

        public Or or(And a) {
                Or o = new Or();
                o.conditions.add(this);
                o.ands.add(a);
                return o;
        }

        public Or or(Or o) {
                o.conditions.add(this);
                return o;
        }

        @Override
        public Or or(ExpressionBoolean expBool) {
                Or o = new Or();
                o.conditions.add(this);
                o.expBools.add(expBool);
                return o;
        }

        @Override
        public boolean isAnd() {
                return false;
        }

        @Override
        public boolean isOr() {
                return false;
        }

        @Override
        public boolean isCondition() {
                return true;
        }

        @Override
        public boolean isExpression() {
                return false;
        }

        @Override
        public String toString() {
                return data + "." + type + args;
        }

        @Override
        public boolean equals(Object o) {
                if (o instanceof Condition) {
                        Condition c = (Condition) o;
                        if (c.type == this.type && c.data == this.data && c.args.size() == this.args.size()) {
                                for (int i = 0; i < this.args.size(); ++i) {
                                        if (!this.args.get(i).equals(c.args.get(i))) return false;
                                }
                                return true;
                        } else {
                                return false;
                        }
                } else {
                        return false;
                }
        }
}
