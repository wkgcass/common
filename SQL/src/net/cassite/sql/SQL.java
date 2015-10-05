package net.cassite.sql;

public class SQL {

        public SelectDistinctClause select() {
                return new SelectDistinctClause(new StringBuilder());
        }

        public SelectClause select(String... cols) {
                return new SelectClauseWithAs(new StringBuilder(), cols, false);
        }

        public SelectClauseWithAs select(String col) {
                return new SelectClauseWithAs(new StringBuilder(), col, false);
        }

        public WhereClause $(String col) {
                return new WhereClause(new StringBuilder('('), col, false);
        }

        public InsertClause insert() {
                return new InsertClause(new StringBuilder());
        }

        public UpdateClause update(String tbl) {
                return new UpdateClause(new StringBuilder(), tbl);
        }

        public DeleteClause delete() {
                return new DeleteClause(new StringBuilder());
        }
}
