package net.cassite.sql;

public class InsertClause {

        public class InsertInto extends SQLEnd<InsertInto> {

                public class InsertIntoCols extends SQLEnd<InsertIntoCols> {

                        public class Values extends SQLEnd<Values> {
                                protected Values(StringBuilder sb, Object... objs) {
                                        super(sb);
                                        sb.append(" values (");
                                        boolean isFirst = true;
                                        for (Object o : objs) {
                                                if (isFirst) {
                                                        isFirst = false;
                                                } else {
                                                        sb.append(", ");
                                                }
                                                sb.append(SQLSyntaxUtils.objToStr(o));
                                        }
                                        sb.append(")");
                                }

                                public Values _(Object... objs) {
                                        sb.append(" (");
                                        boolean isFirst = true;
                                        for (Object o : objs) {
                                                if (isFirst) {
                                                        isFirst = false;
                                                } else {
                                                        sb.append(", ");
                                                }
                                                sb.append(SQLSyntaxUtils.objToStr(o));
                                        }
                                        sb.append(")");
                                        return this;
                                }
                        }

                        protected InsertIntoCols(StringBuilder sb, String... cols) {
                                super(sb);
                                sb.append(" (");
                                boolean isFirst = true;
                                for (String col : cols) {
                                        if (isFirst) {
                                                isFirst = false;
                                        } else {
                                                sb.append(", ");
                                        }
                                        SQLSyntaxUtils.appendSplitDots(sb, col);
                                }
                                sb.append(")");
                        }

                        public SQLEnd<InsertIntoCols> _(SQLEnd<?> select) {
                                sb.append(" (").append(select.toString()).append(")");
                                return this;
                        }

                        public Values values(Object... objs) {
                                return new Values(sb, objs);
                        }
                }

                protected InsertInto(StringBuilder sb, String tbl) {
                        super(sb);
                        sb.append(" into ").append(tbl);
                }

                public InsertIntoCols _(String... cols) {
                        return new InsertIntoCols(sb, cols);
                }

                public SQLEnd<InsertInto> values(Object... objs) {
                        sb.append(" values (");
                        boolean isFirst = true;
                        for (Object o : objs) {
                                if (isFirst) {
                                        isFirst = false;
                                } else {
                                        sb.append(", ");
                                }
                                sb.append(SQLSyntaxUtils.objToStr(o));
                        }

                        sb.append(")");
                        return this;
                }

        }

        protected StringBuilder sb;

        protected InsertClause(StringBuilder sb) {
                this.sb = sb;
                sb.append("insert");
        }

        public InsertInto into(String tbl) {
                return new InsertInto(sb, tbl);
        }
}
