package net.cassite.sql;

import net.cassite.sql.WhereClause.Operator.WhereEnd;

public class JoinClause {

        public class On {

                public class Operator extends WhereGroupOrderStarter<Operator> {
                        protected Operator(StringBuilder sb) {
                                super(sb);
                        }

                        protected Operator(StringBuilder sb, String operator, String o) {
                                super(sb);
                                sb.append(" ").append(operator).append(" ").append(o);
                        }

                        public On on(String col) {
                                return new On(sb, col);
                        }

                        public JoinClause join(String tbl) {
                                sb.append(')');
                                return new JoinClause(sb, tbl);
                        }

                        @Override
                        public String toString() {
                                return super.toString() + ")";
                        }

                        @Override
                        public WhereClause where(String col) {
                                sb.append(')');
                                return super.where(col);
                        }

                        @Override
                        public WhereClause where(WhereEnd... wheres) {
                                sb.append(")");
                                return super.where(wheres);
                        }

                        @Override
                        public ExistsWhereClause whereExists(SQLEnd<?> select) {
                                sb.append(")");
                                return super.whereExists(select);
                        }

                        @Override
                        public ExistsWhereClause whereNotExists(SQLEnd<?> select) {
                                sb.append(")");
                                return super.whereNotExists(select);
                        }

                        @Override
                        public GroupByClause groupBy(String col) {
                                sb.append(')');
                                return super.groupBy(col);
                        }

                        @Override
                        public OrderByClause orderBy(String col) {
                                sb.append(")");
                                return super.orderBy(col);
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

                private StringBuilder sb;

                protected On(StringBuilder sb, String col) {
                        this.sb = sb;
                        sb.append(" on ");
                        SQLSyntaxUtils.appendSplitDots(sb, col);
                }

                public Operator $eq(Object obj) {
                        return new Operator(sb, "=", SQLSyntaxUtils.objToStr(obj));
                }

                public Operator $ne(Object obj) {
                        return new Operator(sb, "<>", SQLSyntaxUtils.objToStr(obj));
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

                public Operator like(String exp) {
                        return new Operator(sb, "like", "`" + exp + "`");
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

        private StringBuilder sb;

        protected JoinClause(StringBuilder sb, String tbl) {
                this.sb = sb;
                sb.replace(sb.indexOf(" from "), sb.indexOf(" from ") + 6, " from (");
                sb.append(" join ");
                SQLSyntaxUtils.appendSplitDots(sb, tbl);
        }

        public On on(String col) {
                return new On(sb, col);
        }

        public JoinClause as(String alias) {
                sb.append(" as `").append(alias).append('`');
                return this;
        }

}
