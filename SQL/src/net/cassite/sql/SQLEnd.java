package net.cassite.sql;

public class SQLEnd<S extends SQLEnd<S>> {
        private static final String[] keysToLine = new String[] { "from ", "join ", "on ", "where ", "group by ", "order by ", "full join ",
                        "left join ", "right join ", "inner join " };
        private static final String[] keysToLineTab = new String[] {};
        private static final String[] keysAfterToLineTab = new String[] { "(" };
        private static final String[] keysToReturn = new String[] { ")" };
        protected StringBuilder sb;

        protected SQLEnd(StringBuilder sb) {
                this.sb = sb;
        }

        public Object $() {
                sb.append(";");
                return this;
        }

        public <T> T $(Class<T> dialect) {
                try {
                        return dialect.getConstructor(StringBuilder.class).newInstance(sb);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @Override
        public String toString() {
                return sb.toString();
        }

        @SuppressWarnings("unchecked")
        public S $(String directlyAppend) {
                sb.append(directlyAppend);
                return (S) this;
        }

        public String pretty() {
                StringBuilder sb = new StringBuilder(this.sb.toString());
                privatePretty(sb, 0, 0);
                return sb.toString();
        }

        private <T> boolean arrayContains(T[] arr, T t) {
                for (T tt : arr) {
                        if ((t == null) ? (tt == null) : (t.equals(tt))) {
                                return true;
                        }
                }
                return false;
        }

        private boolean arrayStartsWith(String[] arr, String str) {
                for (String s : arr) {
                        if ((str == null && s == null) || s.startsWith(str)) {
                                return true;
                        }
                }
                return false;
        }

        private String tabs(int tabs) {
                String toReturn = "";
                for (int i = 0; i < tabs; ++i) {
                        toReturn += "\t";
                }
                return toReturn;
        }

        private int privatePretty(StringBuilder sb, final int tabs, final int currentIndex) {
                int cursor = currentIndex;
                int end;
                out: do {
                        end = cursor >= sb.length() ? cursor : cursor + 1;
                        String retrieved = null;
                        boolean found = false;
                        int atIndex = -1;
                        while (true) {
                                if (!arrayContains(keysToLine, retrieved) && !arrayContains(keysToLineTab, retrieved)
                                                && !arrayContains(keysAfterToLineTab, retrieved) && !arrayContains(keysToReturn, retrieved)
                                                && cursor != end) {
                                        retrieved = sb.substring(cursor, end);
                                        if (arrayStartsWith(keysToLine, retrieved) || arrayStartsWith(keysToLineTab, retrieved)
                                                        || arrayStartsWith(keysAfterToLineTab, retrieved)
                                                        || arrayStartsWith(keysToReturn, retrieved)) {
                                                if (end != sb.length()) {
                                                        ++end;
                                                }
                                        } else {
                                                ++cursor;
                                                if (cursor == end) {
                                                        if (end == sb.length()) {
                                                                break out;
                                                        } else {
                                                                ++end;
                                                        }
                                                }
                                        }
                                } else {
                                        if (cursor != end) {
                                                found = true;
                                                atIndex = cursor;
                                        }
                                        break;
                                }
                        }
                        if (found) {
                                if (arrayContains(keysToReturn, retrieved)) {
                                        sb.insert(atIndex, "\n" + tabs(tabs - 1));
                                        return end + tabs;
                                } else if (arrayContains(keysToLine, retrieved)) {
                                        sb.insert(atIndex, "\n" + tabs(tabs));
                                        cursor += (1 + tabs + retrieved.length());
                                } else if (arrayContains(keysToLineTab, retrieved)) {
                                        sb.insert(atIndex, "\n" + tabs(tabs + 1));
                                        cursor = privatePretty(sb, tabs + 1, cursor + 2 + tabs + retrieved.length());
                                } else if (arrayContains(keysAfterToLineTab, retrieved)) {
                                        sb.insert(atIndex + retrieved.length(), "\n" + tabs(tabs + 1));
                                        cursor = privatePretty(sb, tabs + 1, cursor + 2 + tabs + retrieved.length());
                                }
                        }
                } while (cursor < sb.length());
                return end;
        }
}
