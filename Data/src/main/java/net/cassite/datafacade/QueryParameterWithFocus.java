package net.cassite.datafacade;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wkgcass on 15/10/13.
 */
class QueryParameterWithFocus extends QueryParameter {
        public final Map<IData<?>, String> focusMap = new LinkedHashMap<IData<?>, String>();

        QueryParameterWithFocus() {
        }

        protected QueryParameterWithFocus(QueryParameter parameter, Focus focuses) {
                if (parameter != null) {
                        this.parameters.putAll(parameter.parameters);
                }
                if (focuses != null) {
                        this.focusMap.putAll(focuses.focusMap);
                }
        }

        public QueryParameter focus(IData<?> data) {
                focusMap.put(data, DataUtils.dataToStringUtil(data).replace('.', '_'));
                return this;
        }

        public QueryParameter focus(IData<?> data, String alias) {
                focusMap.put(data, alias);
                return this;
        }

        @Override
        public String toString() {
                return "params:" + parameters + ",focus:" + focusMap;
        }
}
