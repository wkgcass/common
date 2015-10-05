package net.cassite.sql;

public class MultipleFromClause extends WhereGroupOrderStarter<MultipleFromClause> {
        protected MultipleFromClause(StringBuilder sb, String... tbls) {
                super(sb);
                sb.append(" from ");
                boolean isFirst = true;
                for (String tbl : tbls) {
                        if (isFirst) {
                                isFirst = false;
                        } else {
                                sb.append(", ");
                        }
                        SQLSyntaxUtils.appendSplitDots(sb, tbl);
                }
        }

        public FromClause and(String tbl) {
                return new FromClause(sb).and(tbl);
        }

}
