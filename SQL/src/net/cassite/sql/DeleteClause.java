package net.cassite.sql;

public class DeleteClause {

        public class DeleteFrom extends WhereGroupOrderStarter<DeleteFrom> {

                protected DeleteFrom(StringBuilder sb, String tbl) {
                        super(sb);
                        sb.append(" from ");
                        SQLSyntaxUtils.appendSplitDots(sb, tbl);
                }

        }

        protected StringBuilder sb;

        protected DeleteClause(StringBuilder sb) {
                this.sb = sb;
                sb.append("delete");
        }

        public DeleteFrom from(String tbl) {
                return new DeleteFrom(sb, tbl);
        }

}
