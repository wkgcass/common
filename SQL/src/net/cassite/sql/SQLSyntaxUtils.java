package net.cassite.sql;

import java.text.DateFormat;
import java.util.Date;

public abstract class SQLSyntaxUtils {
        private SQLSyntaxUtils() {
        }

        public static void appendSplitDots(StringBuilder sb, String coltbl) {
                if (coltbl.indexOf('.') == -1) {
                        sb.append('`').append(coltbl).append('`');
                } else {
                        String k1 = coltbl.substring(0, coltbl.indexOf('.'));
                        String k2 = coltbl.substring(coltbl.indexOf('.') + 1);
                        sb.append('`').append(k1).append("`.`").append(k2).append('`');
                }
        }

        public static String dateToStr(Date date) {
                // TODO
                return DateFormat.getDateTimeInstance().format(date);
        }

        public static String objToStr(Object o) {
                // TODO
                if (o instanceof Number) {
                        return o.toString();
                }
                if (o instanceof Date) {
                        return dateToStr((Date) o);
                }
                return "`" + o.toString() + "`";
        }

        public static String splitDots(String coltbl) {
                if (coltbl.indexOf('.') == -1) {
                        return '`' + coltbl + '`';
                } else {
                        String k1 = coltbl.substring(0, coltbl.indexOf('.'));
                        String k2 = coltbl.substring(coltbl.indexOf('.') + 1);
                        return '`' + k1 + "`.`" + k2 + '`';
                }
        }
}
