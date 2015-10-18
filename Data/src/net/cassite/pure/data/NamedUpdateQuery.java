package net.cassite.pure.data;

/**
 * Created by wkgcass on 15/10/18.
 */
public abstract class NamedUpdateQuery extends NamedQuery {
    protected NamedUpdateQuery(String name) {
        super(name);
    }

    public abstract void execute();
}
