package net.cassite.pure.data;

import java.util.List;

/**
 * Created by wkgcass on 15/10/18.
 */
public abstract class NamedListQuery<En> extends NamedQuery {
    protected NamedListQuery(String name) {
        super(name);
    }

    public abstract List<En> list();
}
