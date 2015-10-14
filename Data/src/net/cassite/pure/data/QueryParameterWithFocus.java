package net.cassite.pure.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wkgcass on 15/10/13.
 */
public class QueryParameterWithFocus extends QueryParameter {
    public List<IData<?>> focusList = new ArrayList<IData<?>>();


    public QueryParameter focus(IData<?> data) {
        focusList.add(data);
        return this;
    }
}
