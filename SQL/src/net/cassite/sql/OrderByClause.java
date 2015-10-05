package net.cassite.sql;

public class OrderByClause extends SQLEnd<OrderByClause> {
        public class Order extends SQLEnd<Order> {

                protected Order(StringBuilder sb, boolean asc) {
                        super(sb);
                        if (asc) {
                                sb.append(" asc");
                        } else {
                                sb.append(" desc");
                        }
                }

                public OrderByClause and(String col) {
                        sb.append(", ");
                        SQLSyntaxUtils.appendSplitDots(sb, col);
                        return OrderByClause.this;
                }
        }

        protected OrderByClause(StringBuilder sb, String col) {
                super(sb);
                sb.append(" order by ");
                SQLSyntaxUtils.appendSplitDots(sb, col);
        }

        public Order asc() {
                return new Order(sb, true);
        }

        public Order desc() {
                return new Order(sb, false);
        }
}
