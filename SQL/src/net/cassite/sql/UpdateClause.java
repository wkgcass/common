package net.cassite.sql;

public class UpdateClause {

        public class UpdateSet extends WhereGroupOrderStarter<UpdateSet> {
                protected UpdateSet(StringBuilder sb, String col, Object value) {
                        super(sb);
                        sb.append(" set ");
                        SQLSyntaxUtils.appendSplitDots(sb, col);
                        sb.append(" = ").append(SQLSyntaxUtils.objToStr(value));
                }

                public UpdateSet and(String col, Object value) {
                        sb.append(", ");
                        SQLSyntaxUtils.appendSplitDots(sb, col);
                        sb.append(" = ").append(SQLSyntaxUtils.objToStr(value));
                        return this;
                }
        }

        protected StringBuilder sb;

        protected UpdateClause(StringBuilder sb, String tbl) {
                this.sb = sb;
                sb.append("update ");
                SQLSyntaxUtils.appendSplitDots(sb, tbl);
        }

        public UpdateSet set(String col, Object value) {
                return new UpdateSet(sb, col, value);
        }

}
