package net.cassite.pure.data;

import java.util.HashMap;
import java.util.Map;

public class QueryParameter {
        public Map<QueryParameterTypes, Object[]> parameters = new HashMap<QueryParameterTypes, Object[]>();

        public QueryParameter() {
        }

        public QueryParameter top(int i) {
                parameters.put(QueryParameterTypes.top, new Object[] { i });
                return this;
        }

        public QueryParameter limit(int start, int end) {
                parameters.put(QueryParameterTypes.limit, new Object[] { start, end });
                return this;
        }

        public QueryParameter orderBy(OrderBase... dataArr) {
                parameters.put(QueryParameterTypes.orderBy, dataArr);
                return this;
        }
}
