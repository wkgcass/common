package net.cassite.pure.data;

import java.util.List;
import java.util.Map;

/**
 * Created by wkgcass on 15/10/18.
 */
public abstract class NamedMapQuery extends NamedQuery<NamedMapQuery> {
    protected NamedMapQuery(String name) {
        super(name);
        QueryContainer.register(name, this);
    }

    public List<Map<String, Object>> map(Query query) {
        return query.execute(this);
    }
}
