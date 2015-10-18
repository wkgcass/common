package net.cassite.pure.data;

import java.util.List;

/**
 * Created by wkgcass on 15/10/18.
 */
public abstract class NamedListQuery<En> extends NamedQuery<NamedListQuery<En>> {
    protected NamedListQuery(String name) {
        super(name);
        QueryContainer.register(name, this);
    }

    public List<En> list(Query query) {
        return query.execute(this);
    }
}
