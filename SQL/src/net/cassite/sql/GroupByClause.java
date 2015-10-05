package net.cassite.sql;

public class GroupByClause extends OrderStarter<GroupByClause> {

        protected GroupByClause(StringBuilder sb, String col) {
                super(sb);
                sb.append(" group by ").append(col);
        }
}
