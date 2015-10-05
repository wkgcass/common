package net.cassite.sql;

public class SelectClauseWithAs extends SelectClause {

        protected SelectClauseWithAs(StringBuilder sb, String col, boolean isDistinct) {
                super(sb, col, isDistinct);
        }

        protected SelectClauseWithAs(StringBuilder sb, String[] cols, boolean isDistinct) {
                super(sb, cols, isDistinct);
        }

        public SelectClause as(String alias) {
                sb.append(" `").append(alias).append('`');
                return this;
        }

}
