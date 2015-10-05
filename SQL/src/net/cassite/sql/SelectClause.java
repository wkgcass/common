package net.cassite.sql;

public abstract class SelectClause extends FromStarter<SelectClause> {

        public class Into extends FromStarter<Into> {
                protected Into(StringBuilder sb, String tbl) {
                        super(sb);
                        SQLSyntaxUtils.appendSplitDots(sb, tbl);
                }

                public FromStarter<?> in(String external) {
                        sb.append(" in '").append(external).append('\'');
                        return this;
                }

        }

        protected SelectClause(StringBuilder sb, String col, boolean isDistinct) {
                super(sb);
                sb.append("select ");
                if (isDistinct) {
                        sb.append("distinct ");
                }
                if (col.trim().equals("*")) {
                        sb.append("*");
                } else {
                        SQLSyntaxUtils.appendSplitDots(sb, col);
                }
        }

        protected SelectClause(StringBuilder sb, String[] cols, boolean isDistinct) {
                super(sb);
                sb.append("select ");
                if (isDistinct) {
                        sb.append("distinct ");
                }
                boolean isFirst = true;
                for (String str : cols) {
                        if (isFirst) {
                                isFirst = false;
                        } else {
                                sb.append(", ");
                        }
                        if (str.trim().equals("*")) {
                                sb.append("*");
                        } else {
                                SQLSyntaxUtils.appendSplitDots(sb, str);
                        }
                }
        }

        public Into into(String tbl) {
                return new Into(sb, tbl);
        }

        public SelectClauseWithAs and(String col) {
                sb.append(", ");
                SQLSyntaxUtils.appendSplitDots(sb, col);
                return (SelectClauseWithAs) this;
        }

}
