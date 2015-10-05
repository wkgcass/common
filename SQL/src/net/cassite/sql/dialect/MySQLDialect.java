package net.cassite.sql.dialect;

import net.cassite.sql.SQLEnd;

public class MySQLDialect extends SQLEnd<MySQLDialect> {
        public MySQLDialect(StringBuilder sb) {
                super(sb);
        }

        public MySQLDialect limit(int m) {
                sb.append(" limit ").append(m);
                return this;
        }

        public MySQLDialect limit(int m, int n) {
                sb.append(" limit ").append(m).append(",").append(n);
                return this;
        }
}
