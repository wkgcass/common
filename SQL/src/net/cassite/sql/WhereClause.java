package net.cassite.sql;

import net.cassite.sql.WhereClause.Operator.WhereEnd;

public class WhereClause extends SQLEnd<WhereClause> {

        public class Operator extends GroupOrderStarter<Operator> {

                public class WhereEnd extends SQLEnd<WhereEnd> {

                        protected WhereEnd(StringBuilder sb, String logic) {
                                this(sb);
                                sb.append(logic).append(' ');
                        }

                        protected WhereEnd(StringBuilder sb) {
                                super(sb);
                                sb.append(") ");
                        }
                }

                protected Operator(StringBuilder sb) {
                        super(sb);
                }

                protected Operator(StringBuilder sb, String op, String o) {
                        super(sb);
                        sb.append(" ").append(op).append(" ").append(o);
                }

                public WhereClause and(String col) {
                        sb.append(" and ");
                        SQLSyntaxUtils.appendSplitDots(sb, col);
                        return WhereClause.this;
                }

                public WhereClause or(String col) {
                        sb.append(" or ");
                        SQLSyntaxUtils.appendSplitDots(sb, col);
                        return WhereClause.this;
                }

                public ExistsWhereClause andExists(SQLEnd<?> select) {
                        sb.append(" and ");
                        return new ExistsWhereClause(sb, false, select);
                }

                public ExistsWhereClause andNotExists(SQLEnd<?> select) {
                        sb.append(" and ");
                        return new ExistsWhereClause(sb, true, select);
                }

                public ExistsWhereClause orExists(SQLEnd<?> select) {
                        sb.append(" or ");
                        return new ExistsWhereClause(sb, false, select);
                }

                public ExistsWhereClause orNotExists(SQLEnd<?> select) {
                        sb.append(" or ");
                        return new ExistsWhereClause(sb, true, select);
                }

                public WhereEnd and() {
                        return new WhereEnd(sb, "and");
                }

                public WhereEnd or() {
                        return new WhereEnd(sb, "or");
                }

                public WhereEnd $() {
                        return new WhereEnd(sb);
                }
        }

        public class NestedOperator extends Operator {

                protected NestedOperator(StringBuilder sb, String operator) {
                        super(sb);
                        sb.append(" ").append(operator);
                }

                public Operator all(SQLEnd<?> select) {
                        sb.append(" all(").append(select.toString()).append(")");
                        return this;
                }

                public Operator any(SQLEnd<?> select) {
                        sb.append(" any(").append(select.toString()).append(")");
                        return this;
                }

                public Operator some(SQLEnd<?> select) {
                        sb.append(" some(").append(select.toString()).append(")");
                        return this;
                }

        }

        public class BetweenClause<C extends Comparable<C>> {

                private StringBuilder sb;

                protected BetweenClause(StringBuilder sb, C c) {
                        this.sb = sb;
                        sb.append(" between ").append(SQLSyntaxUtils.objToStr(c));
                }

                public Operator and(C c) {
                        sb.append(" and ").append(SQLSyntaxUtils.objToStr(c));
                        return new Operator(sb);
                }
        }

        protected WhereClause(StringBuilder sb, String col, boolean addWhere) {
                super(sb);
                if (addWhere) {
                        sb.append(" where ");
                } else {
                        sb.append("(");
                }
                SQLSyntaxUtils.appendSplitDots(sb, col);
        }

        protected WhereClause(StringBuilder sb, String col) {
                this(sb, col, true);
        }

        protected WhereClause(StringBuilder sb, Operator.WhereEnd... wheres) {
                super(sb);
                sb.append(" where ");
                for (WhereEnd we : wheres) {
                        sb.append(we.toString());
                }
        }

        public Operator $eq(Object o) {
                return new Operator(sb, "=", SQLSyntaxUtils.objToStr(o));
        }

        public Operator $ne(Object o) {
                return new Operator(sb, "<>", SQLSyntaxUtils.objToStr(o));
        }

        public Operator $gt(Comparable<?> c) {
                return new Operator(sb, ">", SQLSyntaxUtils.objToStr(c));
        }

        public Operator $lt(Comparable<?> c) {
                return new Operator(sb, "<", SQLSyntaxUtils.objToStr(c));
        }

        public Operator $gte(Comparable<?> c) {
                return new Operator(sb, ">=", SQLSyntaxUtils.objToStr(c));
        }

        public Operator $lte(Comparable<?> c) {
                return new Operator(sb, "<=", SQLSyntaxUtils.objToStr(c));
        }

        public <C extends Comparable<C>> BetweenClause<C> between(C c) {
                return new BetweenClause<C>(sb, c);
        }

        public Operator like(String exp) {
                return new Operator(sb, "like", "`" + exp + "`");
        }

        private String generateWithPar(Object... objs) {
                StringBuilder newSb = new StringBuilder('(');
                boolean isFirst = true;
                for (Object o : objs) {
                        if (isFirst) {
                                isFirst = false;
                        } else {
                                newSb.append(", ");
                        }
                        newSb.append(SQLSyntaxUtils.objToStr(o));
                }
                newSb.append(')');
                return newSb.toString();
        }

        public Operator in(Object... objs) {
                return new Operator(sb, "in", generateWithPar(objs));
        }

        public Operator notIn(Object... objs) {
                return new Operator(sb, "not in", generateWithPar(objs));
        }

        public Operator eq(String col) {
                return new Operator(sb, "=", SQLSyntaxUtils.splitDots(col));
        }

        public Operator ne(String col) {
                return new Operator(sb, "<>", SQLSyntaxUtils.splitDots(col));
        }

        public Operator gt(String col) {
                return new Operator(sb, ">", SQLSyntaxUtils.splitDots(col));
        }

        public Operator lt(String col) {
                return new Operator(sb, "<", SQLSyntaxUtils.splitDots(col));
        }

        public Operator gte(String col) {
                return new Operator(sb, ">=", SQLSyntaxUtils.splitDots(col));
        }

        public Operator lte(String col) {
                return new Operator(sb, "<=", SQLSyntaxUtils.splitDots(col));
        }

        public Operator in(SQLEnd<?> select) {
                return new Operator(sb, "in", select.toString());
        }

        public Operator notIn(SQLEnd<?> select) {
                return new Operator(sb, "not in", select.toString());
        }

        public NestedOperator eq() {
                return new NestedOperator(sb, "=");
        }

        public NestedOperator ne() {
                return new NestedOperator(sb, "<>");
        }

        public NestedOperator gt() {
                return new NestedOperator(sb, ">");
        }

        public NestedOperator lt() {
                return new NestedOperator(sb, "<");
        }

        public NestedOperator gte() {
                return new NestedOperator(sb, ">=");
        }

        public NestedOperator lte() {
                return new NestedOperator(sb, "<=");
        }

}
