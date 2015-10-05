package net.cassite.sql;

public class SelectDistinctClause {
        private StringBuilder sb;

        protected SelectDistinctClause(StringBuilder sb) {
                this.sb = sb;
        }

        public SelectClause distinct(String... cols) {
                SelectClause s = new SelectClauseWithAs(sb, cols, true);
                return s;
        }

}
