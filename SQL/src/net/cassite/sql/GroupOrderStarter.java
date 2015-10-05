package net.cassite.sql;

public class GroupOrderStarter<G extends GroupOrderStarter<G>> extends OrderStarter<G> {

        protected GroupOrderStarter(StringBuilder sb) {
                super(sb);
        }

        public GroupByClause groupBy(String col) {
                return new GroupByClause(sb, col);
        }

}
