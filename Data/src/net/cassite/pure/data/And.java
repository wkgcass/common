package net.cassite.pure.data;

import java.util.ArrayList;
import java.util.List;

public class And implements Where, AndOr {
        final List<Condition> conditions = new ArrayList<Condition>();
        final List<Or> ors = new ArrayList<Or>();

        public And and(Condition condition) {
                conditions.add(condition);
                return this;
        }

        public And and(And a) {
                this.conditions.addAll(a.conditions);
                this.ors.addAll(a.ors);
                return this;
        }

        public And and(Or o) {
                ors.add(o);
                return this;
        }

        public Or or(Condition condition) {
                Or o = new Or();
                o.conditions.add(condition);
                o.ands.add(this);
                return o;
        }

        public Or or(Or o) {
                o.ands.add(this);
                return o;
        }

        public Or or(And a) {
                Or o = new Or();
                o.ands.add(this);
                o.ands.add(a);
                return o;
        }

        @Override
        public List<And> getAndList() {
                return null;
        }

        @Override
        public List<Or> getOrList() {
                return ors;
        }

        @Override
        public List<Condition> getConditionList() {
                return conditions;
        }

        @Override
        public boolean isAnd() {
                return true;
        }

        @Override
        public boolean isOr() {
                return false;
        }

        @Override
        public ExpressionType expType() {
                return null;
        }

        @Override
        public Object[] expArgs() {
                return null;
        }

        @Override
        public boolean isCondition() {
                return false;
        }

        @Override
        public boolean isExpression() {
                return false;
        }
}
