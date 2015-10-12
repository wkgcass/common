package net.cassite.pure.data;

public class Query {
    private final DataAccess dataAccess;

    public Query(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public <En> From<En> from(Class<En> entityClass) {
        return new From<En>(entityClass, dataAccess);
    }

    @SuppressWarnings("unchecked")
    public <En> From<En> from(En entity) {
        return new From<En>((Class<En>) entity.getClass(), dataAccess);
    }

    public void save(Object... entities) {
        dataAccess.save(entities);
    }

    public <E, T extends Iterable<E>> T find(Class<E> res, String query) {
        return dataAccess.find(res, query);
    }

    public void execute(String query) {
        dataAccess.execute(query);
    }
}
