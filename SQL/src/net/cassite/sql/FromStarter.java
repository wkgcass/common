package net.cassite.sql;

public class FromStarter<F extends FromStarter<F>> {
        protected StringBuilder sb;

        protected FromStarter(StringBuilder sb) {
                this.sb = sb;
        }

        public FromClause from(String tbl) {
                return new FromClause(sb, tbl);
        }

        public MultipleFromClause from(String... tbls) {
                return new MultipleFromClause(sb, tbls);
        }

        public FromClause from(SQLEnd<?> select) {
                return new FromClause(sb, select);
        }
}
