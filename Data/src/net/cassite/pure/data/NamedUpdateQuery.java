package net.cassite.pure.data;

/**
 * Created by wkgcass on 15/10/18.
 */
public abstract class NamedUpdateQuery extends NamedQuery<NamedUpdateQuery> {
    protected NamedUpdateQuery(String name) {
        super(name);
        QueryContainer.register(name, this);
    }

    public void execute(Query query) {
        query.execute(this);
    }
}
