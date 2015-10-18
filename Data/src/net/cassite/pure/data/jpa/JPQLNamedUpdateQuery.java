package net.cassite.pure.data.jpa;

import net.cassite.pure.data.NamedUpdateQuery;

/**
 * Created by wkgcass on 15/10/18.
 */
public class JPQLNamedUpdateQuery extends NamedUpdateQuery {
    final String updateQuery;

    JPQLNamedUpdateQuery(String name, String query) {
        super(name);
        this.updateQuery = query;
    }
}
