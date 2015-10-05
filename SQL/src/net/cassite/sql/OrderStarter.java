package net.cassite.sql;

public class OrderStarter<O extends OrderStarter<O>> extends SQLEnd<O> {

        protected OrderStarter(StringBuilder sb) {
                super(sb);
        }

        public OrderByClause orderBy(String col) {
                return new OrderByClause(sb, col);
        }

}
