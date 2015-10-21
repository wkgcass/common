package net.cassite.pure.data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wkgcass on 15/10/13.
 */
public class QueryParameterWithFocus extends QueryParameter {
    public Map<IData<?>, String> focusMap = new LinkedHashMap<IData<?>, String>();

    public QueryParameterWithFocus() {
    }

    protected QueryParameterWithFocus(QueryParameter parameter) {
        this.parameters = parameter.parameters;
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
