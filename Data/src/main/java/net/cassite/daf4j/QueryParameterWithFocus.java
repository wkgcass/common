package net.cassite.daf4j;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 在需要映射(projection)时使用的QueryParameter,由PreResult对象根据Focus和QueryParameter对象生成生成,不在外部创建
 *
 * @see PreResult#projection(Focus)
 * @see Focus
 * @see QueryParameter
 */
public class QueryParameterWithFocus extends QueryParameter {
        public final Map<IData<?>, String> focusMap = new LinkedHashMap<IData<?>, String>();

        public QueryParameterWithFocus(QueryParameter parameter, Focus focuses) {
                if (parameter != null) {
                        this.parameters.putAll(parameter.parameters);
                }
                if (focuses != null) {
                        this.focusMap.putAll(focuses.focusMap);
                }
        }

        public QueryParameter focus(IData<?> data) {
                return focus(data, DataUtils.dataToStringUtil(data));
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
