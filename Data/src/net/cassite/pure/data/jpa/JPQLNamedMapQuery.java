package net.cassite.pure.data.jpa;

import net.cassite.pure.data.NamedMapQuery;
import net.cassite.pure.data.QueryParameterWithFocus;

/**
 * Created by wkgcass on 15/10/18.
 */
public class JPQLNamedMapQuery extends NamedMapQuery {
    final String selectQuery;
    final QueryParameterWithFocus parameter;

    JPQLNamedMapQuery(String name, String query, QueryParameterWithFocus parameter) {
        super(name);
        this.selectQuery = query;
        this.parameter = parameter;
    }
}
