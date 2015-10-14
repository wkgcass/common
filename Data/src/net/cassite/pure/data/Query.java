package net.cassite.pure.data;

public class Query {
    private final DataAccess dataAccess;

    public Query(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @SuppressWarnings("unchecked")
    public <En> From<En> from(En entity) {
        return new From<En>(entity, dataAccess);
    }

    public void save(Object... entities) {
        dataAccess.save(entities);
    }

    public <E, T extends Iterable<E>> T find(Class<E> res, String query) {
        return dataAccess.find(res, query, null);
    }

    public <E, T extends Iterable<E>> T find(Class<E> res, String query, QueryParameter parameter) {
        return dataAccess.find(res, query, parameter);
    }

    public void execute(String query) {
        dataAccess.execute(query);
    }
}
