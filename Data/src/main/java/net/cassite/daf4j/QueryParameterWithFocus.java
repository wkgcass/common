package net.cassite.daf4j;

import net.cassite.daf4j.util.Selectable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 在需要映射(select)时使用的QueryParameter,由PreResult对象根据Focus和QueryParameter对象生成生成,不在外部创建
 *
 * @see PreResult#select(Focus)
 * @see Focus
 * @see QueryParameter
 */
public class QueryParameterWithFocus extends QueryParameter {
        public final Map<Selectable, String> focusMap = new LinkedHashMap<Selectable, String>();

        public QueryParameterWithFocus(QueryParameter parameter, Focus focuses) {
                if (parameter != null) {
                        this.parameters.putAll(parameter.parameters);
                }
                if (focuses != null) {
                        this.focusMap.putAll(focuses.focusMap);
                }
        }

        public QueryParameterWithFocus focus(IData<?> data) {
                return focus(data, DataUtils.dataToStringUtil(data));
        }

        public QueryParameterWithFocus focus(Selectable selectable, String alias) {
                focusMap.put(selectable, alias);
                return this;
        }

        @Override
        public String toString() {
                return "params:" + parameters + ",focus:" + focusMap;
        }
}
