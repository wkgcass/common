package net.cassite.sql;

import net.cassite.sql.WhereClause.Operator;
import net.cassite.sql.WhereClause.Operator.WhereEnd;

public class ExistsWhereClause extends SQLEnd<ExistsWhereClause> {

        protected ExistsWhereClause(StringBuilder sb, boolean notExists, SQLEnd<?> select) {
                super(sb);
                if (notExists) {
                        sb.append("not exists");
                } else {
                        sb.append("exists");
                }
                sb.append("(").append(select.toString()).append(")");
        }

        public WhereClause and(String col) {
                sb.append(" and ");
                SQLSyntaxUtils.appendSplitDots(sb, col);
                return new WhereClause(sb);
        }

        public WhereClause or(String col) {
                sb.append(" or ");
                SQLSyntaxUtils.appendSplitDots(sb, col);
                return new WhereClause(sb);
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
                WhereClause w = new WhereClause(sb);
                Operator o = w.new Operator(sb);
                return o.new WhereEnd(sb, "and");
        }

        public WhereEnd or() {
                WhereClause w = new WhereClause(sb);
                Operator o = w.new Operator(sb);
                return o.new WhereEnd(sb, "and");
        }

        public WhereEnd $() {
                WhereClause w = new WhereClause(sb);
                Operator o = w.new Operator(sb);
                return o.new WhereEnd(sb, "and");
        }

}
