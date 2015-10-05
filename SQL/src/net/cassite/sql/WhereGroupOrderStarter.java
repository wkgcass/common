package net.cassite.sql;

import net.cassite.sql.WhereClause.Operator.WhereEnd;

public class WhereGroupOrderStarter<W extends WhereGroupOrderStarter<W>> extends GroupOrderStarter<W> {

        protected WhereGroupOrderStarter(StringBuilder sb) {
                super(sb);
        }

        public WhereClause where(String col) {
                return new WhereClause(sb, col);
        }

        public WhereClause where(WhereEnd... wheres) {
                return new WhereClause(sb, wheres);
        }

        public ExistsWhereClause whereExists(SQLEnd<?> select) {
                sb.append(" where ");
                return new ExistsWhereClause(sb, false, select);
        }

        public ExistsWhereClause whereNotExists(SQLEnd<?> select) {
                return new ExistsWhereClause(sb, true, select);
        }

}
