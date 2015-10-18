package net.cassite.pure.data;

import java.util.List;
import java.util.Map;

/**
 * Created by wkgcass on 15/10/18.
 */
public abstract class NamedMapQuery extends NamedQuery {
    protected NamedMapQuery(String name) {
        super(name);
    }

    public abstract List<Map<String, Object>> map();
}
