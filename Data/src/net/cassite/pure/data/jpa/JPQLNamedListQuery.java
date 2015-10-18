package net.cassite.pure.data.jpa;

import net.cassite.pure.data.NamedListQuery;

import javax.persistence.Query;
import java.util.List;

/**
 * Created by wkgcass on 15/10/18.
 */
public class JPQLNamedListQuery<En> extends NamedListQuery<En> {

    final String queryString;

    JPQLNamedListQuery(String name, String queryString) {
        super(name);
        this.queryString = queryString;
    }
}
