package net.cassite.sql;

public class FromClause extends WhereGroupOrderStarter<FromClause> {

        public class As extends WhereGroupOrderStarter<As> {
                protected As(StringBuilder sb, String alias) {
                        super(sb);
                        sb.append(" as `").append(alias).append('`');
                }

                public FromClause and(String tbl) {
                        return FromClause.this.and(tbl);
                }

                public FromClause and(SQLEnd<?> select) {
                        return FromClause.this.and(select);
                }

                public JoinClause join(String tbl) {
                        return new JoinClause(sb, tbl);
                }

                public JoinClause leftJoin(String tbl) {
                        sb.append(" left");
                        return new JoinClause(sb, tbl);
                }

                public JoinClause innerJoin(String tbl) {
                        sb.append(" inner");
                        return new JoinClause(sb, tbl);
                }

                public JoinClause rightJoin(String tbl) {
                        sb.append(" right");
                        return new JoinClause(sb, tbl);
                }

                public JoinClause fullJoin(String tbl) {
                        sb.append(" full");
                        return new JoinClause(sb, tbl);
                }
        }

        protected FromClause(StringBuilder sb) {
                super(sb);
        }

        protected FromClause(StringBuilder sb, @SuppressWarnings("rawtypes") SQLEnd select) {
                super(sb);
                sb.append(" from (").append(select.toString()).append(")");
        }

        protected FromClause(StringBuilder sb, String tbl) {
                super(sb);
                sb.append(" from ");
                SQLSyntaxUtils.appendSplitDots(sb, tbl);
        }

        public As as(String alias) {
                return new As(sb, alias);
        }

        public FromClause and(String tbl) {
                sb.append(", ");
                SQLSyntaxUtils.appendSplitDots(sb, tbl);
                return this;
        }

        public FromClause and(SQLEnd<?> select) {
                sb.append(", (").append(select.toString()).append(")");
                return this;
        }

        public JoinClause join(String tbl) {
                return new JoinClause(sb, tbl);
        }

        public JoinClause leftJoin(String tbl) {
                sb.append(" left");
                return new JoinClause(sb, tbl);
        }

        public JoinClause innerJoin(String tbl) {
                sb.append(" inner");
                return new JoinClause(sb, tbl);
        }

        public JoinClause rightJoin(String tbl) {
                sb.append(" right");
                return new JoinClause(sb, tbl);
        }

        public JoinClause fullJoin(String tbl) {
                sb.append(" full");
                return new JoinClause(sb, tbl);
        }
}
